import json
import boto3


def lambda_handler(event, context):
    dynamodb = boto3.resource('dynamodb', region_name='us-east-1',
                              aws_secret_access_key="YOUR_AWS_SECRET_ACCESS_KEY",
                              aws_access_key_id="YOUR_AWS_SECRET_ACCESS_KEY_ID")

    recipe_table = dynamodb.Table("recipe")
    response = recipe_table.get_item(Key={"recipe_id": event["recipe_id"]})
    recipe_item = (response["Item"] if "Item" in response else None)

    for key in recipe_item:
        recipe_item[key] = str(recipe_item[key])

    return {
        'statusCode': 200,
        'body': json.dumps(recipe_item)
    }
