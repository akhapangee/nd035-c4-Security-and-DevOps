# Search Commands
## CreateUser request successes
```
source="app.log" host="Akhileshs-MBP.lan" index="e-commerce_app_index" sourcetype="e-commerce-app-source" "User Creation Success"
```

## CreateUser request failures
```
source="app.log" host="Akhileshs-MBP.lan" index="e-commerce_app_index" sourcetype="e-commerce-app-source" "User Creation Failure"
```

## Exceptions
```
source="app.log" host="Akhileshs-MBP.lan" index="e-commerce_app_index" sourcetype="e-commerce-app-source" "exception"
```

## Order requests successes
```
source="app.log" host="Akhileshs-MBP.lan" index="e-commerce_app_index" sourcetype="e-commerce-app-source" "Order Request Success"
```

## Order requests failures
```
source="app.log" host="Akhileshs-MBP.lan" index="e-commerce_app_index" sourcetype="e-commerce-app-source" "Order Request Failure"
```

## Create dashboard for success rate per minute of any one CreateUser and order, and take a screenshot.

### User creation success:
```
source="app.log" host="Akhileshs-MBP.lan" index="e-commerce_app_index" sourcetype="e-commerce-app-source" "User Creation Success" | bucket _time span=1m  | stats count by _time request
```

### Order creation success:
```
source="app.log" host="Akhileshs-MBP.lan" index="e-commerce_app_index" sourcetype="e-commerce-app-source" "Order Request Success" | bucket _time span=1m  | stats count by _time request
```