package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

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
        mode2 (output mode):

    gamepad2:
        left_bumper: cycle mode to left
        right_bumper: cycle mode to right

        mode1 ()
     */

    @Override
    public void runOpMode() throws InterruptedException {
        double drive;
        double strafe;
        double rotate;

        double flPower;
        double frPower;
        double blPower;
        double brPower;

        char last_button1 = '\0';
        char last_button2 = '\0';

        double mf = 0.25;

        DcMotor fl = null;
        DcMotor fr = null;
        DcMotor bl = null;
        DcMotor br = null;

        CRServo lis = null;
        CRServo ris = null;

        CRServo ts1 = null;
        CRServo ts2 = null;

        DcMotor vs = null;
        DcMotor vsm = null;

        try {
            fl = hardwareMap.get(DcMotor.class, "FLWheel");
            fr = hardwareMap.get(DcMotor.class, "FRWheel");
            bl = hardwareMap.get(DcMotor.class, "BLWheel");
            br = hardwareMap.get(DcMotor.class, "BRWheel");
        } catch (IllegalArgumentException _) {
        }

        try {
            lis = hardwareMap.get(CRServo.class, "LeftIntakeServo");
            ris = hardwareMap.get(CRServo.class, "RightIntakeServo");
        } catch (IllegalArgumentException _) {
        }

        try {
            ts1 = hardwareMap.get(CRServo.class, "TrayServo1");
            ts2 = hardwareMap.get(CRServo.class, "TrayServo2");
        } catch (IllegalArgumentException _) {
        }

        try {
            vs = hardwareMap.get(DcMotor.class, "ViperSlide");
            vsm = hardwareMap.get(DcMotor.class, "SlideMotor");
        } catch (IllegalArgumentException _) {
        }

        //fr.setDirection(DcMotorSimple.Direction.REVERSE);
        //br.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        while (opModeIsActive()) {
            drive = gamepad1.left_stick_y;
            strafe = -gamepad1.left_stick_x;
            rotate = -gamepad1.right_stick_x;

            flPower = drive - strafe + rotate;
            frPower = drive - strafe - rotate;
            blPower = drive + strafe + rotate;
            brPower = drive + strafe - rotate;

            /*
            fl.setPower(mf * flPower);
            fr.setPower(mf * frPower);
            bl.setPower(mf * blPower);
            br.setPower(mf * brPower);
             */
            // Temporarily controllable for all – testing required to know what intakes and what outtakes
            if (gamepad1.a) lis.setPower(-1);
            if (gamepad1.b) lis.setPower(1);
            if (gamepad1.x) ris.setPower(-1);
            if (gamepad1.y) ris.setPower(1);

            if (gamepad1.back) lis.setPower(0);
            if (gamepad1.start) ris.setPower(0);
            /*
            vs.setPower(gamepad1.left_stick_y);

            vsm.setPower(-gamepad1.left_trigger);
            vsm.setPower(gamepad1.right_trigger);
             */

            // Temporarily controllable for all – testing required to know what intakes and what outtakes
            if (gamepad2.a) {
                lis.setPower(-1);
                last_button1 = 'a';
            }
            if (gamepad2.b) {
                lis.setPower(1);
                last_button1 = 'b';
            }
            if (gamepad2.x) {
                ris.setPower(-1);
                last_button2 = 'x';
            }
            if (gamepad2.y) {
                ris.setPower(1);
                last_button2 = 'y';
            }

            if (gamepad2.left_stick_x > 0.2 || gamepad2.left_stick_x < -0.2)
                lis.setPower(0);
            if (gamepad2.right_stick_x > 0.2 || gamepad2.right_stick_x < -0.2)
                ris.setPower(0);

            if (gamepad2.back) {
                ts1.setPower(0.5);
                ts2.setPower(0.5);
            }
            if (gamepad2.start) {
                ts1.setPower(-0.5);
                ts2.setPower(-0.5);
            }
            if (!gamepad2.back && !gamepad2.start) {
                ts1.setPower(0);
                ts2.setPower(0);
            }

            /*
            telemetry.addData("flPos", fl.getCurrentPosition());
            telemetry.addData("frPos", fr.getCurrentPosition());
            telemetry.addData("blPos", bl.getCurrentPosition());
            telemetry.addData("brPos", br.getCurrentPosition());
             */

            telemetry.addData("last_button1", last_button1);
            telemetry.addData("last_button2", last_button2);

            telemetry.update();
        }
    }
}
