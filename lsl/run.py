#!/bin/python

import sys
import httplib
import urlparse
import json
import time
import signal

base_url = urlparse.urlparse(sys.argv[1])
base_path = base_url.path

def create_repl():
    conn = httplib.HTTPConnection(base_url.netloc)
    try:
        print base_path
        conn.request("POST", base_path)
        resp = conn.getresponse()

        if resp.status != 200:
            raise "Failed: "+resp.reason

        repl = json.loads(resp.read())
        print repl
        return repl
    finally:
        conn.close()

def delete_repl(repl):
    conn = httplib.HTTPConnection(base_url.netloc)
    try:
        conn.request("DELETE",base_path+"/"+repl["id"])
        conn.getresponse()
    finally:
        conn.close()

def send_repl(repl,line):
    conn = httplib.HTTPConnection(base_url.netloc)
    try:
        conn.request("POST",base_path+"/"+repl["id"],line)
        resp = conn.getresponse()
        return {'status': resp.status, 'result': resp.read()}
    finally:
        conn.close()

def get_repl(repl):
    conn = httplib.HTTPConnection(base_url.netloc)
    try:
        conn.request("GET",base_path+"/"+repl["id"])
        resp = conn.getresponse()
        if 200 == resp.status:
            return json.loads(resp.read())
        else:
            return None
    finally:
        conn.close()

class Terminated(Exception):
    pass

def sigterm_handler(x,y):
    raise Terminated()

repl = create_repl()
signal.signal(signal.SIGTERM,sigterm_handler)

try:
    sys.stdout.write("user> ")
    cmd = sys.stdin.readline()

    while cmd != None:
        resp = send_repl(repl,cmd)
        if resp['status'] == 204:
            resp = get_repl(repl)
            while resp['state'] == 'evaluating':
                time.sleep(1)
                resp = get_repl(repl)
        sys.stdout.write(str(resp['result'])+"\n")
        sys.stdout.write("user> ")
        cmd = sys.stdin.readline()
except KeyboardInterrupt:
    None
except Terminated:
    print "Terminated"
finally:
    delete_repl(repl)
