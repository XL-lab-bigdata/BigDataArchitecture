# models.py - comprehensive-case-webapp
#
# Copyright 2023 Jinsong Zhang
#
# This file can NOT be copied and/or distributed without the express permission of Jinsong Zhang

from pydantic import BaseModel


class Text(BaseModel):
    raw: str


class SegText(BaseModel):
    raw: str
    words: list[str]
