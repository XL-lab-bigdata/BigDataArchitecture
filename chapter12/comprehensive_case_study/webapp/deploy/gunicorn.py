# gunicorn.py - comprehensive-case-webapp
#
# Copyright 2023 Jinsong Zhang
#
# This file can NOT be copied and/or distributed without the express permission of Jinsong Zhang

import os

daemon = True
bind = "127.0.0.1:8000"
pidfile = "gunicorn.pid"
chdir = "."  # 项目地址
worker_class = "uvicorn.workers.UvicornWorker"
workers = 2
threads = 1
worker_connections = 1000
loglevel = "info"  # 日志级别
access_log_format = '%(t)s %(p)s %(h)s "%(r)s" %(s)s %(L)s %(b)s %(f)s" "%(a)s"'
log_dir = "./log"
if not os.path.exists(log_dir):
    os.makedirs(log_dir)
accesslog = "./log/gunicorn_access.log"
errorlog = "./log/gunicorn_error.log"
