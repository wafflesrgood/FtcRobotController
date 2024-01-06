package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp

public class IntakeServoTesting extends LinearOpMode {

    private CRServo IntakeL, IntakeR;

    public void runOpMode() throws InterruptedException {
        telemetry.addData("Mode", "waiting");
        telemetry.update();

        //Initialization of Intake Wheels
        IntakeR = hardwareMap.get(CRServo.class, "IntakeR");
        IntakeL = hardwareMap.get(CRServo.class, "IntakeL");
        //


        waitForStart();
        while (opModeIsActive()) {
            ////CODE///
           IntakeR.setPower(1); //test change in direction
           IntakeL.setPower(-1);

        }
    }
}
