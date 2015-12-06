import requests
r = requests.post("http://127.0.0.1:5000/", data={'@number': 12524, '@type': 'issue'})
print(r.status_code, r.reason)
print(r.text[:300] + '...')
