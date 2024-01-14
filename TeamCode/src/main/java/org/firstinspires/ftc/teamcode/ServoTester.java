package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class ServoTester extends LinearOpMode {

    @Override
    public void runOpMode() {
        // Initialize hardware
        Servo c1 = hardwareMap.get(Servo.class, "Claw1");
        Servo c2 = hardwareMap.get(Servo.class, "Claw2");

        waitForStart();

        telemetry.addData("C1 Pos.", c1.getPosition());
        telemetry.addData("C2 Pos.", c2.getPosition());
        telemetry.update();

        double position1 = 0;
        double position2 = 0;
        // TODO: figure out why controlling one servo (moving one joystick) moves another servo as well
        while (opModeIsActive()) {
            if (gamepad1.a) {
                position1 += 0.005;
            }
            if (gamepad1.b) {
                position1 -= 0.005;
            }
            if (gamepad1.x) {
                position1 += 0.001;
            }
            if (gamepad1.y) {
                position1 -= 0.001;
            }

            if (gamepad2.a) {
                position2 += 0.005;
            }
            if (gamepad2.b) {
                position2 -= 0.005;
            }
            if (gamepad2.x) {
                position2 += 0.001;
            }
            if (gamepad2.y) {
                position2 -= 0.001;
            }

            c1.setPosition(position1);
            c2.setPosition(position2);

            telemetry.addData("Pos 1", position1);
            telemetry.addData("Pos 2", position2);
            telemetry.update();

            sleep(25);
        }
    }
}