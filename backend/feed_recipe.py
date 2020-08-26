import json
import pandas as pd
import time
import boto3
import numpy as np
data =pd.read_csv('./recipe/RAW_recipes.csv',header=0,sep=',')

dynamodb = boto3.resource('dynamodb', region_name='us-east-1',
                          aws_secret_access_key="XXTsIHVZGnlqw8do2V1Ch9zl+/rVRnteEPxlXELc",
                          aws_access_key_id="AKIAWNFF4BE7J2BEL6FL")
recipe_table = dynamodb.Table("recipe")
data = data.replace(np.nan,None)


with recipe_table.batch_writer() as batch:
    for i in range(len(data)):
        line = data.iloc[i]
        item = {"recipe_id": str(line["id"]),
                "name": line["name"],
                "minutes": int(line["minutes"]),
                "tags": line["tags"],
                "n_steps": int(line["n_steps"]),
                "steps": line["steps"],
                "description": line["description"],
                "ingredients": line["ingredients"],
                "n_ingredients": int(line["n_ingredients"]),
                }

        batch.put_item(
            Item=item
        )

