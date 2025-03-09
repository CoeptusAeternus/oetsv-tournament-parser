import pika
import logging
import os
from smtplib import SMTP
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

from .models.mail import Mail, NewTournamentMail, NennschlussMail
from .models.pika_message import PikaMessage

SMTP_SERVER = os.environ.get('SMTP_SERVER')
SMTP_PORT = os.environ.get('SMTP_PORT')
SMTP_USERNAME = os.environ.get('SMTP_USERNAME')
SMTP_PASSWORD = os.environ.get('SMTP_PASSWORD')

RABBITMQ_HOST = os.environ.get('RABBITMQ_HOST')
RABBITMQ_PORT = os.environ.get('RABBITMQ_PORT')
RABBITMQ_USERNAME = os.environ.get('RABBITMQ_USERNAME')
RABBITMQ_PASSWORD = os.environ.get('RABBITMQ_PASSWORD')

RECIPIENTS = os.environ.get('RECIPIENTS').split(',')
RECIPIENTS = map(lambda x: x.strip(), RECIPIENTS)

if not SMTP_SERVER or not SMTP_PORT or not SMTP_USERNAME or not SMTP_PASSWORD:
    raise ValueError("SMTP_SERVER, SMTP_PORT, SMTP_USERNAME, SMTP_PASSWORD must be set")

if not RABBITMQ_HOST or not RABBITMQ_PORT or not RABBITMQ_USERNAME or not RABBITMQ_PASSWORD:
    raise ValueError("RABBITMQ_HOST, RABBITMQ_PORT, RABBITMQ_USERNAME, RABBITMQ_PASSWORD must be set")

def send_email(mail: Mail):
    
    subject, body, sender = mail.format_for_sending(RECIPIENTS)
    
    msg = MIMEMultipart()
    msg['From'] = sender
    msg['Subject'] = subject
    msg.attach(MIMEText(body, 'plain', 'utf-8'))    
    
    logging.debug(f"attempting mail server login on {SMTP_SERVER}:{SMTP_PORT}")
    try:
        with SMTP(SMTP_SERVER, SMTP_PORT) as smtp:
            logging.debug(f"attempting mail server login with {SMTP_USERNAME}")
            smtp.login(SMTP_USERNAME, SMTP_PASSWORD)
            
            logging.debug(f"attempting to send email to {RECIPIENTS}")
            for recipient in RECIPIENTS:
                msg['To'] = recipient
                smtp.send_message(msg.as_string)
                
    except Exception as e:
        logging.error(f"Failed to send email: {e}")
        return

def message_callback(ch, method, properties, body):
    
    logging.debug(f"Received message: {body}")
    
    try:
        message = PikaMessage.model_validate_json(body)
    except Exception as e:
        logging.error(f"Failed to validate message: {e}")
        return
    
    logging.info(f"Recieved message of type {message.type} for tournament {message.tournament.id}")
    
    if message.type == "NewTournament":
        mail = NewTournamentMail(message.tournament)
    elif message.type == "Nennschluss":
        mail = NennschlussMail(message.tournament)
    else:
        raise ValueError("Unknown message type")
    
    send_email(mail)
    

def main():
    
    logging.basicConfig(level=logging.INFO)
    
    logging.info("Starting mailer")
    logging.info(f"conneting to rabbitmq at {RABBITMQ_HOST}:{RABBITMQ_PORT}")
    connection = pika.BlockingConnection(pika.ConnectionParameters(
        host=RABBITMQ_HOST,
        port=RABBITMQ_PORT,
        credentials=pika.PlainCredentials(RABBITMQ_USERNAME, RABBITMQ_PASSWORD)
    ))
    
    channel = connection.channel()
    channel.queue_declare(queue='mails')
    
    channel.basic_consume(queue='mails', on_message_callback=message_callback, auto_ack=True)
    
    logging.info("Waiting for messages")
    channel.start_consuming()
    
    
if __name__ == "__main__":
    main()