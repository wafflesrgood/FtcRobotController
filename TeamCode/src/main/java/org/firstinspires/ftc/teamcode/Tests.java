package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class Tests extends LinearOpMode {
    private DcMotor test;
    private float power = 1F;
    private float step = 0.01F;
    private int reverse = 0;
    private long delay = 1;

    @Override
    public void runOpMode() {
        // Initialize hardware
        test = hardwareMap.get(DcMotor.class, "Test");
        test.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();

        while (opModeIsActive()) {
            double hangPower = -gamepad1.left_stick_y;
            test.setPower(hangPower);
        }
    }
}