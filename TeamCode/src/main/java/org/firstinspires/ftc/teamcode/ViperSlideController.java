package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class ViperSlideController extends LinearOpMode {
    private DcMotor ViperSlide;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Waiting");
        telemetry.update();

        ViperSlide = hardwareMap.get(DcMotor.class, "ViperSlide");

        waitForStart();
        while (opModeIsActive()) {
            ViperSlide.setPower(-gamepad1.left_stick_y * 0.3);
        }
    }
}
