import boto3
from pprint import pprint


dynamodb = boto3.resource('dynamodb', region_name='us-east-1',
                          aws_secret_access_key="Btx/jq9HVZ6AO0ivkb7AJX0Cfm7QHG2Sd9VHOUc2",
                          aws_access_key_id="AKIAWNFF4BE7O5NWP6VW")
recipe_table = dynamodb.Table("user")
response = recipe_table.scan()
pprint(response)

# dynamoDBClient = boto3.client('dynamodb', region_name='us-east-1',
#                           aws_secret_access_key="Btx/jq9HVZ6AO0ivkb7AJX0Cfm7QHG2Sd9VHOUc2",
#                           aws_access_key_id="AKIAWNFF4BE7O5NWP6VW")
# recipe_table = dynamoDBClient.describe_table(
#     TableName='recipe'
# )