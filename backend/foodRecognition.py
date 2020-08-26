import json
import urllib3


def lambda_handler(event, context):
    url = "https://api.clarifai.com/v2/models/bd367be194cf45149e75f01d59f77ba7/outputs"
    api_key = 'YOUR_AWS_SECRET_ACCESS_KEY'
    headers = {
        "Content-Type": "application/json",
        "Authorization": "Key {}".format(api_key)
    }
    data = {
        "inputs": [
            {
                "data": {
                    "image": {
                        "url": event["picture_url"]
                    }
                }
            }
        ]
    }

    data = json.dumps(data)
    http = urllib3.PoolManager()
    response = http.request('POST', url=url, body=data, headers=headers)
    data = response.data
    data = data.decode()

    data = json.loads(data)
    data = data["outputs"][0]["data"]["concepts"]

    res_dic = []
    for item in data:
        name = item["name"]
        confidence = item["value"]
        res_dic.append({"name": name, "confidence": confidence})

    res = json.dumps(res_dic)

    return {
        'statusCode': 200,
        'body': res
    }
