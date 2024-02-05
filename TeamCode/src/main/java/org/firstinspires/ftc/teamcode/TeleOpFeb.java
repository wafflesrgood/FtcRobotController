package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.Arrays;

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
        //                         drive, tray intakes, tray rotators, slides, main intakes
        boolean activeSystems[] = {false, false,        false,         false,         false};
        boolean allSystemsGo[] = {true, true, true, true, true};

        double drive;
        double strafe;
        double rotate;

        double flPower;
        double frPower;
        double blPower;
        double brPower;

        //            movement factor, tray rotator factor
        double f[] = {0.25,            0.35                };

        DcMotor fl = null;
        DcMotor fr = null;
        DcMotor bl = null;
        DcMotor br = null;

        CRServo lis = null;
        CRServo ris = null;

        CRServo mouth = null;
        CRServo anus = null;

        CRServo ts1 = null;
        CRServo ts2 = null;

        DcMotor vs = null;
        DcMotor vsm = null;

        try {
            fl = hardwareMap.get(DcMotor.class, "frontleft");
            fr = hardwareMap.get(DcMotor.class, "frontright");
            bl = hardwareMap.get(DcMotor.class, "backleft");
            br = hardwareMap.get(DcMotor.class, "backright");

            activeSystems[0] = true;
        } catch (IllegalArgumentException e) {
        }

        try {
            mouth = hardwareMap.crservo.get("mouth");
            anus = hardwareMap.crservo.get("anus");

            activeSystems[1] = true;
        } catch (IllegalArgumentException e) {
        }

        try {
            ts1 = hardwareMap.get(CRServo.class, "leftboxrotator");
            //ts2 = hardwareMap.get(CRServo.class, "rightboxrotator");

            activeSystems[2] = true;
        } catch (IllegalArgumentException e) {
        }

        try {
            vs = hardwareMap.get(DcMotor.class, "viperslidemotor");
            vsm = hardwareMap.get(DcMotor.class, "viperslidepullmotor");

            activeSystems[3] = true;
        } catch (IllegalArgumentException e) {
        }

        try {
            lis = hardwareMap.get(CRServo.class, "leftintake");
            ris = hardwareMap.get(CRServo.class, "rightintake");

            activeSystems[4] = true;
        } catch (IllegalArgumentException e) {
        }

        telemetry.addData("Drivetrain Active", activeSystems[0]);
        telemetry.addData("Tray Intakes Active", activeSystems[1]);
        telemetry.addData("Tray Rotators Active", activeSystems[2]);
        telemetry.addData("Slides Active", activeSystems[3]);
        telemetry.addData("Main Intakes Active", activeSystems[4]);

        if (Arrays.equals(activeSystems, allSystemsGo)) {
            telemetry.addLine("All systems go.");
        } else {
            telemetry.addLine("All systems are not initialized. Be prepared for a NullPointerException.");
        }

        telemetry.update();

        fr.setDirection(DcMotorSimple.Direction.REVERSE);
        br.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        while (opModeIsActive()) {
            // g1

            drive = gamepad1.left_stick_y;
            strafe = -gamepad1.left_stick_x;
            rotate = -gamepad1.right_stick_x;

            flPower = drive - strafe + rotate;
            frPower = drive - strafe - rotate;
            blPower = drive + strafe + rotate;
            brPower = drive + strafe - rotate;


            fl.setPower(f[0] * flPower);
            fr.setPower(f[0] * frPower);
            bl.setPower(f[0] * blPower);
            br.setPower(f[0] * brPower);


            // g2

            // intake on
            if (gamepad2.a) {
                mouth.setPower(1);
                lis.setPower(-1);
                ris.setPower(1);
            }

            // output on
            if (gamepad2.b) {
                anus.setPower(1);
            }

            if (gamepad2.y) {
                anus.setPower(-1);
            }

            // intake off
            if (gamepad2.left_bumper) {
                mouth.setPower(0);
                lis.setPower(0);
                ris.setPower(0);
            }

            // output off
            if (gamepad2.right_bumper)
                anus.setPower(0);

            // tray rotators
            if (gamepad2.back) {
                ts1.setPower(f[1]);
                //ts2.setPower(-f[1]);
            }
            if (gamepad2.start) {
                ts1.setPower(-f[1]);
                //ts2.setPower(-f[1]);
            }
            if (!gamepad2.back && !gamepad2.start) {
                ts1.setPower(0);
                //ts2.setPower(0);
            }

            vs.setPower(gamepad2.left_stick_y);
            vsm.setPower(gamepad2.right_stick_y);

            telemetry.addData("flPos", fl.getCurrentPosition());
            telemetry.addData("frPos", fr.getCurrentPosition());
            telemetry.addData("blPos", bl.getCurrentPosition());
            telemetry.addData("brPos", br.getCurrentPosition());

            telemetry.update();
        }
    }
}
