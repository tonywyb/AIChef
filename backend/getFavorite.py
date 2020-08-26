import json
import boto3


def lambda_handler(event, context):
    user_id = event["user_id"]
    dynamodb = boto3.resource('dynamodb', region_name='us-east-1',
                              aws_secret_access_key="YOUR_AWS_SECRET_ACCESS_KEY",
                              aws_access_key_id="YOUR_AWS_SECRET_ACCESS_KEY")

    visitors_table = dynamodb.Table("user")
    recipe_table = dynamodb.Table("recipe")

    response = visitors_table.get_item(Key={"user_id": user_id})
    user = (response["Item"] if "Item" in response else None)
    if user == None:
        item = {"user_id": user_id, }
        response = visitors_table.put_item(Item=item)
        response = visitors_table.get_item(Key={"user_id": user_id})
        user = (response["Item"] if "Item" in response else None)
    favorite = (user["favorite"] if "favorite" in user else [])

    res = []

    for item in favorite:
        response = recipe_table.get_item(Key={"recipe_id": item})
        recipe_item = (response["Item"] if "Item" in response else None)

    res.append(recipe_item)

    return {
        'statusCode': 200,
        'body': json.dumps(res)
    }
