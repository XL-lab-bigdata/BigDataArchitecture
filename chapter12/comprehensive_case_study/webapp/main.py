# main.py - comprehensive-case-webapp
#
# Copyright 2023 Jinsong Zhang
#
# This file can NOT be copied and/or distributed without the express permission of Jinsong Zhang

import jieba
from fastapi import FastAPI

from comprehensive_case_webapp.models import SegText, Text

app = FastAPI()


@app.post("/")
async def segment(text: Text) -> SegText:
    words = jieba.cut(text.raw)
    return SegText(raw=text.raw, words=list(words))

