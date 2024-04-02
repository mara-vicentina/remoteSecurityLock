#include <Servo.h>

#define buzzerPin 2
#define servoMotorPin 8
#define redLedPin 9
#define greenLedPin 10

int unlockPassword = 1111;
int lockStatus = 0;
int numberOfFailedAttempts = 0;

Servo servoMotor;

void setup() {
  Serial.begin(9600);
  pinMode(buzzerPin, OUTPUT);
  pinMode(redLedPin, OUTPUT);
  pinMode(greenLedPin, OUTPUT);
  servoMotor.attach(servoMotorPin);
  servoMotor.write(180);
}

void loop() {

  while (Serial.available() == 0);

  String recievedValue = Serial.readString();
  char actionValue = getValueBySeparator(recievedValue, ':', 0).charAt(0);
  String secundaryValue = getValueBySeparator(recievedValue, ':', 1);
  int secundaryValueFormated = secundaryValue.toInt();

  if (actionValue == 'f') {
    lock();
  }

  if (actionValue == 'a') {
    unlock(secundaryValueFormated);
  }
}

void lock() {
  servoMotor.write(180);
  lockStatus = 0;
  digitalWrite(redLedPin, HIGH);
  delay(1500);
  digitalWrite(redLedPin, LOW);

  numberOfFailedAttempts = 0;
}

void unlock(int secundaryValueFormated) {
  if (secundaryValueFormated != unlockPassword) {
    numberOfFailedAttempts++;

    if (numberOfFailedAttempts > 2) {
      for (int i=0; i < 5; i++) {
        digitalWrite(redLedPin, HIGH);
        tone(buzzerPin, 349, 300);
        delay(500);
        noTone(buzzerPin);
        digitalWrite(redLedPin, LOW);
        delay(500);
      }

      return;
    }
    
    if (lockStatus == 1) {
      lock();
      return;
    }
    
    digitalWrite(redLedPin, HIGH);
    tone(buzzerPin, 349, 300);
    delay(1000);
    noTone(buzzerPin);
    digitalWrite(redLedPin, LOW);
    return;
  }

  servoMotor.write(0);
  lockStatus = 1;
  digitalWrite(greenLedPin, HIGH);
  delay(1500);
  digitalWrite(greenLedPin, LOW);
}

String getValueBySeparator(String data, char separator, int index) {
  int found = 0;
  int strIndex[] = {0, -1};
  int maxIndex = data.length()-1;

  for(int i=0; (i <= maxIndex && found <= index); i++) {
    if(data.charAt(i) == separator || i == maxIndex){
        found++;
        strIndex[0] = strIndex[1] + 1;
        strIndex[1] = (i == maxIndex) ? i + 1 : i;
    }
  }

  return (found > index) ? data.substring(strIndex[0], strIndex[1]) : "";
}
