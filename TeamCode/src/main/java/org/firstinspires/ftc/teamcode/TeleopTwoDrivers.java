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

    public void runOpMode() throws InterruptedException {
        telemetry.addData("Mode", "waiting");
        telemetry.update();
        //Initialization of Arm Motor
        ArmMotor = hardwareMap.get(DcMotor.class, "ArmMotor");
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

        waitForStart();
        while (opModeIsActive()) {
            ////CODE FOR DRIVETRAIN ///
            double drive = -gamepad1.left_stick_x;
            double strafe = gamepad1.left_stick_y;
            double rotate = -gamepad1.right_stick_x;
            double frontLeftPower = Range.clip(drive + strafe + rotate, -1.0, 1.0);
            double frontRightPower = Range.clip(drive - strafe - rotate, -1.0, 1.0);
            double backLeftPower = Range.clip(drive - strafe + rotate, -1.0, 1.0);
            double backRightPower = Range.clip(drive + strafe - rotate, -1.0, 1.0);
            frontLeft.setPower(0.5 * frontLeftPower);
            frontRight.setPower(0.5 * frontRightPower);
            backLeft.setPower(0.5 * backLeftPower);
            backRight.setPower(0.5 * backRightPower);
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
            double ArmPower = -gamepad2.right_stick_y;
            ArmMotor.setPower(0.4 * ArmPower);
            //

            //Wrist Servo Control
            if (gamepad2.right_bumper) { JointServo.setPosition(0.78);}
            if (gamepad2.left_bumper) { JointServo.setPosition(0.9);}
            //
            //Claw Servo Control
            if (gamepad2.a) { ClawServo.setPosition(0.65);}
            if (gamepad2.b) { ClawServo.setPosition(0.48);}
            if (gamepad2.y) { ClawServo.setPosition(1); }

            //Intake mill controls (GAMEPAD 1)
            if (gamepad1.right_bumper) { Intake.setPower(0.5);}
            if (gamepad1.left_bumper) { Intake.setPower(-0.5); }
            if (gamepad1.a) { Intake.setPower(0);}
            if (gamepad2.x) { Drone.setPosition(0.8);}
        }
    }
}
