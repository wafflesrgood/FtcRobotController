/*
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class ViperSlideController extends LinearOpMode {
    private DcMotor vs, vsm;
    private CRServo s1, s2;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Waiting");
        telemetry.update();

        vsm = hardwareMap.get(DcMotor.class, "SlideMotor");
        vs = hardwareMap.get(DcMotor.class, "ViperSlide");
        s1 = hardwareMap.get(CRServo.class, "LeftIntakeServo"); // 4
        s2 = hardwareMap.get(CRServo.class, "RightIntakeServo"); // 5

        char last_button1, last_button2 = 'z';

        waitForStart();
        while (opModeIsActive()) {
            vs.setPower(gamepad1.left_stick_y);

            vsm.setPower(-gamepad1.left_trigger);
            vsm.setPower(gamepad1.right_trigger);

            if (gamepad1.a) {
                s1.setPower(1);
                last_button1 = 'a';
            }
            if (gamepad1.b) {
                s2.setPower(1);
                last_button2 = 'b';
            }
            if (gamepad1.x) {
                s1.setPower(-1);
                last_button1 = 'x';
            }
            if (gamepad1.y) {
                s2.setPower(-1);
                last_button2 = 'y';
            }

            if (gamepad1.back)
                s1.setPower(0);
            if (gamepad1.start)
                s2.setPower(0);

            telemetry.addData("last_button1", last_button1);
            telemetry.addData("last_button2", last_button2);

            telemetry.update();
        }
    }
}
*/