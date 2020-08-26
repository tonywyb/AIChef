import boto3
import requests
import json
import time
from pprint import pprint
from boto3.dynamodb.conditions import Key, Attr
from requests_aws4auth import AWS4Auth


dynamodb = boto3.resource('dynamodb', region_name='us-east-1',
                          aws_secret_access_key="YOUR_AWS_SECRET_ACCESS_KEY",
                          aws_access_key_id="YOUR_AWS_ACCESS_KEY_ID")
recipe_table = dynamodb.Table("recipe")
response = recipe_table.scan()

host = '*'
region = 'us-east-1'
service = 'es'
access_access_key_id = "*"
access_secrect_access_key = "*"
awsauth = AWS4Auth(access_access_key_id, access_secrect_access_key, region, service)
pprint(len(response["Items"]))


counter = 0
for i in range(0, len(response["Items"])):
    counter += 1
    item = response["Items"][i]
    if i % 100 == 0:
        print("finished:", counter, "/", len(response["Items"]))
    document = {"Ingredients": item["ingredients"]}
    r = requests.put(host+"/recipes"+"/Recipe/"+item["recipe_id"], auth=awsauth, json=document,
                      headers={"Content-Type": "application/json"})


while 'LastEvaluatedKey' in response:
    response = recipe_table.scan(ExclusiveStartKey=response['LastEvaluatedKey'])
    for i in range(0, len(response["Items"])):
        counter += 1
        item = response["Items"][i]
        if i % 100 == 0:
            print("finished:", counter, "/", len(response["Items"]))
        document = {"Ingredients": item["ingredients"]}
        r = requests.put(host + "/recipes" + "/Recipe/" + item["recipe_id"], auth=awsauth, json=document,
                         headers={"Content-Type": "application/json"})

