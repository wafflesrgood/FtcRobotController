package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class IntakeServoTesting extends LinearOpMode {

    private CRServo intakeL, intakeR;

    public void runOpMode() throws InterruptedException {
        telemetry.addData("Mode", "waiting");
        telemetry.update();

        //Initialization of Intake Wheels
        intakeR = hardwareMap.crservo.get( "RightIntakeServo");

        intakeL = hardwareMap.get(CRServo.class, "LeftIntakeServo");
        //

       intakeR.resetDeviceConfigurationForOpMode();
        waitForStart();


        while (opModeIsActive()) {
            ////CODE///
           intakeR.setPower(1);
           intakeL.setPower(-1);

        }
    }
}
