package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Servo;

public class RedAuto extends LinearOpMode {

    DcMotor rightDrive;
    DcMotor leftDrive;
    DcMotorController driveController;

    Servo rightRescuer;
    Servo leftRescuer;

    LightSensor lineSensor;
    ColorSensor allianceSensor;

    //Write this when making an autonomous program
    @Override
    public void runOpMode() throws InterruptedException {
        rightDrive = hardwareMap.dcMotor.get("right_drive");
        leftDrive = hardwareMap.dcMotor.get("left_drive");
        driveController = hardwareMap.dcMotorController.get("drive");

        rightRescuer = hardwareMap.servo.get("right_res");
        leftRescuer = hardwareMap.servo.get("left_res");

        lineSensor = hardwareMap.lightSensor.get("line_sensor");
        allianceSensor = hardwareMap.colorSensor.get("ally_sensor");

        //reverses the right motor
        rightDrive.setDirection(DcMotor.Direction.REVERSE);

        //turns on the LED on the color sensor
        allianceSensor.enableLed(true);

        waitForStart();

        //Time it takes to move 1 floor block (or 60 cm) below, calculations based off of that.
        long blockTime = 2280;

        //Time it takes to turn a quarter-turn (90 degrees) below, calculations based off of that.
        //Reminder that these are in ms
        long turnTime = 4750;

        //half of this (45 degrees) is...
        long halfTurnTime = 2375;

        //Starting to actually move, better scream it to the world so everyone knows
        telemetry.addData("Done?", "Nope!");

        //We don't need this on all the time.
        allianceSensor.enableLed(false);

        //Move from start to beacon
        forward(blockTime);
        turnLeft(halfTurnTime);
        while (lineSensor.getLightDetected() < 0.5){
            forward(0); //Go until you get to the guiding line (if bad, this is about 2.828 blocks)
        }

        //Light Sensor Stuff for Beacon
        if(checkColor()){
            //red alliance is detected
        } else {
            //blue alliance is detected
        }
        //Move back a little bit so you turn cleanly
        rightDrive.setPower(-1.0);
        leftDrive.setPower(-1.0);
        sleep(500);

        //Move from Beacon to mountain on other side
        turnRight(turnTime * 2);
        forward(blockTime * 2);
        turnLeft(halfTurnTime);
        forward(blockTime * 2);

        //Stop moving after
        rightDrive.setPower(0);
        leftDrive.setPower(0);

        //tell the world you're done and jus' chillin' yo
        telemetry.clearData();
        telemetry.addData("Done?", "Completed! :D");
    }

    public void forward(long blockTime) throws InterruptedException{
        rightDrive.setPower(1.0);
        leftDrive.setPower(1.0);
        sleep(blockTime);
        telemetry.addData("Status", "Going Forward!");
    } //both motors on

    public void turnRight(long turnTime) throws InterruptedException{
        rightDrive.setPower(1.0);
        leftDrive.setPower(-1.0);
        sleep(turnTime);
        telemetry.addData("Status", "Turning Right!");
    } // left motor reversed, right on

    public void turnLeft(long turnTime) throws InterruptedException{
        rightDrive.setPower(-1.0);
        leftDrive.setPower(1.0);
        sleep(turnTime);
        telemetry.addData("Status", "Turning Left!");
    } // right motor reversed, left on

    public boolean checkColor() throws InterruptedException{
        telemetry.addData("Clear", allianceSensor.alpha());
        telemetry.addData("Red  ", allianceSensor.red());
        telemetry.addData("Green", allianceSensor.green());
        telemetry.addData("Blue ", allianceSensor.blue());
        return (allianceSensor.red() > allianceSensor.blue());
    } //true if red, false if blue

}   //TO-DO: Write what happens for both colors the beacon could be (rescuers, button press)
    //Write how the light sensor will be used to approach the bacon and align with it
    //Keep this matched with tele-op on new parts added to the robot.
    //Get those two measurements ASAP and test!