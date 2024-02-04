package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
public class ServoTester extends LinearOpMode {

    @Override
    public void runOpMode() {
        // Initialize hardware
        CRServo ts1 = hardwareMap.crservo.get("TrayServo1");
        CRServo ts2 = hardwareMap.crservo.get("TrayServo1");

        ts2.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();
        while (opModeIsActive()) {

            telemetry.update();
        }
        ts1.setPower(0);
    }
}