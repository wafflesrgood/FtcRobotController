package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class ServoTester extends LinearOpMode {
    private Servo servo;
    private Servo vss;

    private static double position1;
    private static double position2;
    private double increment = 0.05D;

    @Override
    public void runOpMode() {
        // Initialize hardware
        servo = hardwareMap.get(Servo.class, "WristServo");
        vss = hardwareMap.get(Servo.class, "ViperSlideServo");

        waitForStart();

        while (opModeIsActive()) {
            position1 = servo.getPosition();
            position2 = vss.getPosition();
            servo.setPosition(position1);
            vss.setPosition(position2);


            if (gamepad1.x) {
                position1 += increment;
            } else if (gamepad1.b) {
                position1 += increment;
            } else if (gamepad2.x) {
                position1 += increment;
            } else if (gamepad2.b) {
                position1 += increment;
            }

            telemetry.addData("Position1", position1);
            telemetry.addData("Position2", position2);
            telemetry.update();

            sleep(50);
        }
    }
}