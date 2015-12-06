import os, time, sys, socket
from flask import Flask, render_template, request, flash, redirect, session, abort
from flask.ext.mail import Message, Mail
from sqlalchemy.orm import sessionmaker
from tabledef import *
import uuid
import hashlib
from OpenSSL import SSL
context = SSL.Context(SSL.SSLv23_METHOD)
context.use_privatekey_file('sc.key')
context.use_certificate_file('sc.crt')

engine = create_engine('sqlite:///tutorial.db', echo=True)
 
# mail = Mail()
app = Flask(__name__) 
state_file = 'state.txt'

def generateToken():
    return uuid.uuid4().get_hex()

def sendToken(token, hash_token):
    #create a TCP/IP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    #connect socket to the port where the server is listening
    server_address = ('localhost', 4444)
    sock.connect(server_address)

    print('Connecting to %s: %s\n' % server_address)


    try:    
        #send token
        sock.sendall(str(token))

        #receive token
        rcv_token = sock.recv(128)
        print 'Received token: ' + rcv_token
        if hash_token == rcv_token:
            return True;
        else:
            return False;
    except:
        print('Exception while sending data\n')
        return False;



@app.route('/')
def home():
    if not session.get('logged_in'):
        return render_template('login.html')
    else:
        return render_template('welcome.html')

@app.route('/login', methods=['POST'])
def login():
    global counter
    POST_USERNAME = str(request.form['username'])
    POST_PASSWORD = str(request.form['password'])
    
    Session = sessionmaker(bind=engine)
    s = Session()
    query = s.query(User).filter(User.username.in_([POST_USERNAME]), User.password.in_([POST_PASSWORD]) )
    result = query.first()
    if result:
        pattern = '19e22522c33d2dff774020cbbff8d62cfa622493'

        #send token
        token = generateToken()
        print token
        hash_token =  hashlib.sha256(token+pattern).hexdigest()
        print hash_token
        if sendToken(token, hash_token):
            session['logged_in'] = True
            return render_template('welcome.html')
        else:
            session['logged_in'] = False
            error = 'Invalid credentials'
            print 'wrong token'
            return render_template('login.html', error=error)
        #session['logged_in'] = True
    #else:
    #    flash('wrong password!')
    #return home()
    #print 'before check\n'
    #if POST_USERNAME == 'a' and POST_PASSWORD == 'b':
        #get pattern from database
        

        '''with open(state_file, 'a') as myfile:
            myfile.write(POST_USERNAME + ',SVR,' + token + '\n')
        myfile.close()'''
        

        '''#wait for token
        for i in range(0,200):
            txt = open(state_file).read().split('\n')
            client_reply = [line for line in reversed(txt) if POST_USERNAME + ',CLNT,' in line]
            print txt
            if client_reply:
                client_token = client_reply[0].split(',')[-1]
                if client_token == token:
                   session['logged_in'] = True
                   return render_template('welcome.html')
            
            time.sleep(2)'''
        '''session['logged_in'] = False
        return home()'''
    else:
        session['logged_in'] = False
        error = 'Invalid credentials'
        print 'wrong'
        #flash('error')
        return render_template('login.html', error=error)
 
@app.route('/m', methods=['GET','POST'])
def mobile_client():
    #get username from post method
    if request.method == 'GET':
        username = str(request.args.get('u'))
        f = open(state_file)
        txt = f.read().split('\n')
        f.close()
        svr_token = [line for line in reversed(txt) if username + ',SVR,' in line]
        if svr_token:
            svr_token = svr_token[0].split(',')[-1]
            return svr_token

    '''if request.method == 'POST':
        data = request.form.get('data')''' #things of m2 will actually be in this post section

@app.route('/m2', methods=['GET','POST'])
def mobile_client2():
    #get username from post method
    if request.method == 'GET':
        data = str(request.args.get('d'))
        with open(state_file, 'a') as myfile:
            #myfile.write(data)
            myfile.write('a' + ',CLNT,' + data + '\n')
        myfile.close()
        return 'done'

'''@app.route('/authenticate')
def authenticate():
    return render_template('authenticate.html')'''

@app.route('/register')
def register():
  return render_template('register.html')

'''@app.route('/welcome')
def welcome():
    return render_template('welcome.html')'''

@app.route("/logout")
def logout():
    session['logged_in'] = False
    return home()


if __name__ == '__main__':
    app.secret_key = os.urandom(12)
    context = ('sc.crt', 'sc.key')
    app.run(debug=False, threaded=True, ssl_context=context)
