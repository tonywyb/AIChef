import json
import boto3


def lambda_handler(event, context):
    user_id = event["user_id"]
    dynamodb = boto3.resource('dynamodb', region_name='us-east-1',
                              aws_secret_access_key="YOUR_AWS_SECRET_ACCESS_KEY",
                              aws_access_key_id="YOUR_AWS_SECRET_ACCESS_KEY_ID")
    visitors_table = dynamodb.Table("user")
    response = visitors_table.get_item(Key={"user_id": user_id})
    user = (response["Item"] if "Item" in response else None)
    if user == None:
        item = {"user_id": user_id, }
        response = visitors_table.put_item(Item=item)
        response = visitors_table.get_item(Key={"user_id": user_id})
        user = (response["Item"] if "Item" in response else None)

    query_history = (user["query_history"] if "query_history" in user else [])

    return {
        'statusCode': 200,
        'body': json.dumps(query_history)
    }
