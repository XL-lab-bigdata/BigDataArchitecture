# locustfile.py - comprehensive-case-webapp
#
# Copyright 2023 Jinsong Zhang
#
# This file can NOT be copied and/or distributed without the express permission of Jinsong Zhang

import random

from locust import HttpUser, between, task

SENTENCES_PATH = "./data/sentences.txt"

SENTENCES: list[str]
with open(SENTENCES_PATH, mode="r", encoding="utf-8") as f:
    SENTENCES = f.readlines()


class SegmentationUser(HttpUser):
    @task
    def send_segment_request(self):
        num = random.randint(1, 100)

        for _ in range(num):
            sentence = random.choice(SENTENCES)
            self.client.post("/", json={"raw": sentence})
