# ibgw-api

Service that provides a simple REST API for IB gateway. It can be used to place orders, get current positions and the account summary.

Each request establishes a new connection to IB gateway and closes it when work is done. This simplifies connection handling and allows
restarts of the Gateway. All requests are synchronized on server side to prevent race conditions.   

Note that the service does not start/stop IB gateway. You will need to do this manually or automate it with an existing solution like [IBC](https://github.com/IbcAlpha/IBC).

## Installation

1. Download the latest [TWS Java API](https://interactivebrokers.github.io/tws-api) and copy it into the `/src` folder, so that the code is located in `/src/main/java/com/ib`

2. Build the service with Maven: `mvn clean package`

3. Build the docker image with `docker build -t ibgw-api .`

4. Start the docker container. Set the `GATEWAY_` environment variables to the host, port and client ID of your IB Gateway instance.
    ```shell
    docker run -d -p 5000:8080 \
              -e 'SPRING_PROFILES_ACTIVE=prod' \
              -e 'GATEWAY_HOST=127.0.0.1' \
              -e 'GATEWAY_PORT=4001' \
              -e 'GATEWAY_CLIENT_ID=1234' \
              -e 'JAVA_TOOL_OPTIONS="-Xms768M -Xmx1G"' \        
              --memory=1024M \
              --name ibgw-api \          
              --restart unless-stopped \
              ibgw-api
    ```

5. Navigate to http://localhost:5000/swagger-ui/index.html#/ to verify that the service is running.

## License

Copyright 2022 Christopher Pawelzik

Licensed under the Apache Software License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0.
