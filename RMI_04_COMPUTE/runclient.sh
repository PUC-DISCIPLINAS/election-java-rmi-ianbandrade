#!/bin/bash

java -cp /home/pucminas/Dev/COMP \
     -Djava.security.policy=client.policy \
       ComputePi 192.168.0.64 45
