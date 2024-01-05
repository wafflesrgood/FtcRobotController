package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp

public class TeleopTwoDrivers extends LinearOpMode {
    DcMotor ArmMotor;
    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private Servo ClawServo;
    private Servo Drone;
    private Servo JointServo;
    private DcMotor Intake;
    private DcMotor Hanger;

    public void runOpMode() throws InterruptedException {
        telemetry.addData("Mode", "waiting");
        telemetry.update();
        //Initialization of Arm Motor
        ArmMotor = hardwareMap.get(DcMotor.class, "ArmMotor");
        ArmMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Hanger = hardwareMap.get(DcMotor.class, "Hanging");
        //
        // Initialization of movement motors
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        //
        //Initialization of Claw and wrist joint
        ClawServo = hardwareMap.get(Servo.class, "ClawServo");
        JointServo = hardwareMap.get(Servo.class, "JointServo");
        //

        //Initialization of Intake motor and Drone servo
        Intake = hardwareMap.get(DcMotor.class, "Intake");
        Drone = hardwareMap.get(Servo.class, "Drone");
        //

        // Set motor directions if needed
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        //multiplier and flippers
        double multiplier = 0.3;
        double flipper = 1;


        waitForStart();
        while (opModeIsActive()) {
            ////CODE FOR DRIVETRAIN ///
            double drive = -gamepad1.left_stick_x;
            double strafe = gamepad1.left_stick_y;
            double rotate = flipper * gamepad1.right_stick_x;
            double frontLeftPower = Range.clip(drive + strafe + rotate, -1.0, 1.0);
            double frontRightPower = Range.clip(drive - strafe - rotate, -1.0, 1.0);
            double backLeftPower = Range.clip(drive - strafe + rotate, -1.0, 1.0);
            double backRightPower = Range.clip(drive + strafe - rotate, -1.0, 1.0);
            frontLeft.setPower(multiplier * frontLeftPower);
            frontRight.setPower(multiplier * frontRightPower);
            backLeft.setPower(multiplier * backLeftPower);
            backRight.setPower(multiplier * backRightPower);
            //Updating telemetry
            telemetry.addData("Status", "Running");
            telemetry.addData("Front Left Power", frontLeftPower);
            telemetry.addData("Front Right Power", frontRightPower);
            telemetry.addData("Back Left Power", backLeftPower);
            telemetry.addData("Back Right Power", backRightPower);
            telemetry.addData("Position", ArmMotor.getCurrentPosition());
            telemetry.update();
            telemetry.update();
            //


            ///ARM MOVEMENT (GAMEPAD 2)
            double ArmPower = gamepad2.right_stick_y;
            ArmMotor.setPower(0.4 * ArmPower);
            //Hanger Movement (GAMEPAD 2)
            double HangPower = gamepad2.left_stick_y;
            Hanger.setPower(HangPower * 0.8);


            //Wrist Servo Control
            if (gamepad2.a) { JointServo.setPosition(0.78);} //for grabbing?
            if (gamepad2.b) { JointServo.setPosition(0.9);} //for lifting?
            if (gamepad2.y) { JointServo.setPosition(1.3); }  //for placing?
            //
            //Claw Servo Control
            if (gamepad2.right_bumper) { ClawServo.setPosition(0.75);} // open
            if (gamepad2.left_bumper) { ClawServo.setPosition(0.5);} // close


            //Intake mill controls (GAMEPAD 1)
            if (gamepad1.right_bumper) { Intake.setPower(0.5);}
            if (gamepad1.left_bumper) { Intake.setPower(-0.5); }
            if (gamepad1.a) { Intake.setPower(0);}

            //Swap Speed! (Gamepad 1)
            if (gamepad1.b) { multiplier = 0.8; }
            if (gamepad1.y) { multiplier = 0.3; }

            //Swap Direction! (Gamepad1)
            if (gamepad1.x) { flipper = -1 * flipper ;}
            //Drone Launch! (Gamepad2)
            if (gamepad2.x)
            {
                Drone.setPosition(-1);
                // Drone.setPosition(1);
                // Drone.setPosition(0.5);
            }
        }
    }
}
