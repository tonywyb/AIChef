import json
import boto3
import datetime
from requests_aws4auth import AWS4Auth
import requests
from boto3.dynamodb.conditions import Key, Attr
from pprint import pprint


def lambda_handler(event, context):
    ingredients = event["ingredients"]
    search_time = event["search_time"]
    picture_url = event["picture_url"]
    user_id = event["user_id"]

    dynamodb = boto3.resource('dynamodb', region_name='us-east-1',
                                  aws_secret_access_key="*",
                                  aws_access_key_id="*")
    recipe_table = dynamodb.Table("recipe")
    visitors_table = dynamodb.Table("user")

    host = '*'
    region = 'us-east-1'
    service = 'es'
    access_access_key_id = "*"
    access_secrect_access_key = "*"
    awsauth = AWS4Auth(access_access_key_id, access_secrect_access_key, region, service)

    # fuzzy search setting:
    # fuzziness: allow up to 2 characters to change
    # fuzzy_max_expansions: expand up to 50 characters for fuzzy matches
    # fuzzy_prefix_length: 1 character at the beginning of terms should not be changed for fuzzy matches
    # TODO: tune query string setting
    json_q = json.dumps({
        "query": {
            "query_string": {
                "query": " ".join(ingredients),
                "fuzziness": 2,
                "default_operator": "OR",
                "fuzzy_max_expansions": 50,
                "fuzzy_prefix_length": 1,
            },
        },
        "size": 20
    })

    headers = {'Content-Type': 'application/json'}
    r = requests.get(host + "/" + "recipes" + "/_search", auth=awsauth, data=json_q, headers=headers)
    res = json.loads(r.text)

    reply = ""
    for i in range(len(res["hits"]["hits"])):
        try:
            recipe_id = res["hits"]["hits"][i]["_id"]
            response = recipe_table.query(KeyConditionExpression=Key("recipe_id").eq(recipe_id))
            reply = json.dumps({
                "recipe_id": recipe_id,
                "minutes": str(response["Items"][0]["minutes"]),
                "name": response["Items"][0]["name"],
                "tags": response["Items"][0]["tags"],
            })
        except Exception as e:
            print(e)

    new_history = {
        'picture_url': picture_url,
        'search_time': search_time,
        'query': ' '.join(ingredients)
    }
    response = visitors_table.get_item(Key={"user_id": user_id})
    user = (response["Item"] if "Item" in response else None)
    if user == None:
        item = {"user_id": user_id, }
        response = visitors_table.put_item(Item=item)
        response = visitors_table.get_item(Key={"user_id": user_id})
        user = (response["Item"] if "Item" in response else None)

    query_history = user["query_history"] if "query_history" in user else []
    query_history.append(new_history)
    query_history_len = len(query_history)
    if query_history_len > 10:
        query_history = query_history[query_history_len - 10:]

    response = visitors_table.update_item(Key={"user_id": user_id},
                                          UpdateExpression="set query_history=:h",
                                          ExpressionAttributeValues={":h": query_history})
    return {
        'statusCode': 200,
        'body': json.dumps(reply)
    }
