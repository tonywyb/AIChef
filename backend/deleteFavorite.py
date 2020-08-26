import json
import boto3


def lambda_handler(event, context):
    # TODO implement
    recipe_id = event["recipe_id"]
    user_id = event["user_id"]

    dynamodb = boto3.resource('dynamodb', region_name='us-east-1',
                              aws_secret_access_key="XXTsIHVZGnlqw8do2V1Ch9zl+/rVRnteEPxlXELc",
                              aws_access_key_id="AKIAWNFF4BE7J2BEL6FL")
    visitors_table = dynamodb.Table("user")
    response = visitors_table.get_item(Key={"user_id": user_id})
    user = (response["Item"] if "Item" in response else None)
    if user == None:
        item = {"user_id": user_id, }
        response = visitors_table.put_item(Item=item)
        response = visitors_table.get_item(Key={"user_id": user_id})
        user = (response["Item"] if "Item" in response else None)

    favorite = (user["favorite"] if "favorite" in user else [])

    for i in range(0, len(favorite)):
        if recipe_id == favorite[i]:
            del favorite[i]
            break

    response = visitors_table.update_item(Key={"user_id": user_id},
                                          UpdateExpression="set favorite=:p",
                                          ExpressionAttributeValues={":p": favorite})

    return {
        'statusCode': 200,
        'body': json.dumps('delete favorite success')
    }
