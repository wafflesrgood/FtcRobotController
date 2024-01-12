package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp

public class TestActuator extends LinearOpMode {

    private Servo LinearServo;
    private DcMotor Motor;

    public void runOpMode() throws InterruptedException {


        //Initialization of Intake motor and Drone servo
        Motor = hardwareMap.get(DcMotor.class, "ViperRotator");

        //


        waitForStart();
        while (opModeIsActive())
        {
            // set servo to desired position
            Motor.setPower(0.5 * gamepad1.right_stick_y);
            //start running the actuator!


        }
    }
}
