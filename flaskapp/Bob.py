# Aniqua Z. Baset
# Homework 2, Network Security, Fall 2015

#I've learned socket programming code from https://pymotw.com/2/socket/tcp.html

import sys, socket
import hashlib


#create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

#bind socket to port
server_address = ('localhost', 4444)
sock.bind(server_address)

print('Bob is listening on %s: %s\n' %server_address)

#listen for incoming connections
sock.listen(1)

while True:
    #wait for a connection
    
    #accept a connection
    connection, client_address = sock.accept()
    try:
        #receive t_a
        token = connection.recv(32)        
        print token
        pattern = '19e22522c33d2dff774020cbbff8d62cfa622493'
        connection.sendall(hashlib.sha256(token+pattern).hexdigest())

    except:
        print 'error'
