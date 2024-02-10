package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class ServoTester extends LinearOpMode {

    @Override
    public void runOpMode() {
        // Initialize hardware
        Servo auto = hardwareMap.servo.get("auto");

        waitForStart();
        while (opModeIsActive()) {
            if (gamepad1.a) {
                auto.setPosition(0.1);
            }
            if (gamepad1.b) {
                auto.setPosition(0.125);
            }
            if (gamepad1.x) {
                auto.setPosition(0.25);
            }
            if (gamepad1.y) {
                auto.setPosition(0.375);
            }

            if (gamepad2.a) {
                auto.setPosition(0.5); // up
            }
            if (gamepad2.b) {
                auto.setPosition(0.625);
            }
            if (gamepad2.x) {
                auto.setPosition(0.75);
            }
            if (gamepad2.y) {
                auto.setPosition(0.825);
            }

            if (gamepad1.back) {
                auto.setPosition(0);
            }
            if (gamepad1.start) {
                auto.setPosition(0.05);
            }

            if (gamepad2.back) {
                auto.setPosition(0.95);
            }
            if (gamepad2.start) {
                auto.setPosition(1);
            }

            telemetry.update();
        }
        auto.setPosition(0);
    }
}