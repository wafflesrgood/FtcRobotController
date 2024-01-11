package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp

public class TestActuator extends LinearOpMode {

    private Servo LinearServo;
    private DcMotor Linear;

    public void runOpMode() throws InterruptedException {


        //Initialization of Intake motor and Drone servo
        Linear = hardwareMap.get(DcMotor.class, "Linear");
        LinearServo = hardwareMap.get(Servo.class, "LinearServo");
        //


        waitForStart();
        while (opModeIsActive())
        {
            // set servo to desired position
            LinearServo.setPosition(2);
            //start running the actuator!


        }
    }
}
