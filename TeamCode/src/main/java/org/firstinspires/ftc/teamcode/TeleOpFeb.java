package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name="TeleOp for February Competition")
public class TeleOpFeb extends LinearOpMode {
    /*
    CONTROLS:

    gamepad1:
        left_bumper: cycle mode to left
        right_bumper: cycle mode to right

        mode1 (movement/intake mode):
            left_stick: move/strafe
            right_stick: rotate
        mode2 ():

    gamepad2:
        left_bumper: cycle mode to left
        right_bumper: cycle mode to right

        mode1 ()
     */

    @Override
    public void runOpMode() throws InterruptedException {
        int mode1 = 1;
        int mode2 = 1;

        double drive;
        double strafe;
        double rotate;

        double flPower;
        double frPower;
        double blPower;
        double brPower;

        char last_button1 = 'z';
        char last_button2 = 'z';

        double mf = 0.25;

        DcMotor fl = hardwareMap.get(DcMotor.class, "FLWheel");
        DcMotor fr = hardwareMap.get(DcMotor.class, "FRWheel");
        DcMotor bl = hardwareMap.get(DcMotor.class, "BLWheel");
        DcMotor br = hardwareMap.get(DcMotor.class, "BRWheel");

        CRServo lis = hardwareMap.get(CRServo.class, "LeftIntakeServo");
        CRServo ris = hardwareMap.get(CRServo.class, "RightIntakeServo");

        DcMotor vs = hardwareMap.get(DcMotor.class, "ViperSlide");
        DcMotor vsm = hardwareMap.get(DcMotor.class, "SlideMotor");

        fr.setDirection(DcMotorSimple.Direction.REVERSE);
        br.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.left_bumper) mode1 -= 1;
            if (gamepad1.right_bumper) mode1 += 1;
            if (gamepad2.left_bumper) mode2 -= 1;
            if (gamepad2.right_bumper) mode2 += 1;

            switch (mode1) {
                case 1: // Intake
                    drive = gamepad1.left_stick_y;
                    strafe = -gamepad1.left_stick_x;
                    rotate = -gamepad1.right_stick_x;

                    flPower = drive - strafe + rotate;
                    frPower = drive - strafe - rotate;
                    blPower = drive + strafe + rotate;
                    brPower = drive + strafe - rotate;

                    fl.setPower(mf * flPower);
                    fr.setPower(mf * frPower);
                    bl.setPower(mf * blPower);
                    br.setPower(mf * brPower);

                    // Temporarily controllable for all – testing required to know what intakes and what outtakes
                    if (gamepad1.a) lis.setPower(-1);
                    if (gamepad1.b) lis.setPower(1);
                    if (gamepad1.x) ris.setPower(-1);
                    if (gamepad1.y) ris.setPower(1);

                    if (gamepad1.back) lis.setPower(0);
                    if (gamepad1.start) ris.setPower(0);
                case 2:
                    vs.setPower(gamepad1.left_stick_y);

                    vsm.setPower(-gamepad1.left_trigger);
                    vsm.setPower(gamepad1.right_trigger);

                    // Temporarily controllable for all – testing required to know what intakes and what outtakes
                    if (gamepad1.a) {
                        lis.setPower(-1);
                        last_button1 = 'a';
                    }
                    if (gamepad1.b) {
                        lis.setPower(1);
                        last_button1 = 'b';
                    }
                    if (gamepad1.x) {
                        ris.setPower(-1);
                        last_button2 = 'x';
                    }
                    if (gamepad1.y) {
                        ris.setPower(1);
                        last_button2 = 'y';
                    }

                    if (gamepad1.back)
                        lis.setPower(0);
                    if (gamepad1.start)
                        ris.setPower(0);
            }

            telemetry.addData("flPos", fl.getCurrentPosition());
            telemetry.addData("frPos", fr.getCurrentPosition());
            telemetry.addData("blPos", bl.getCurrentPosition());
            telemetry.addData("brPos", br.getCurrentPosition());

            telemetry.addData("mode1", mode1);
            telemetry.addData("mode2", mode2);

            telemetry.addData("last_button1", last_button1);
            telemetry.addData("last_button2", last_button2);

            telemetry.update();
        }
    }
}
