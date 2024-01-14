package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="TeleOp for January Competition")
public class TeleOpJan extends LinearOpMode {

    // Webcam is plugged into the control hub.


    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Waiting");
        telemetry.update();
        // Initialization of Control Hub things
        // Motors
        // Control Hub
        DcMotor FLWheel = hardwareMap.get(DcMotor.class, "FLWheel");
        DcMotor BLWheel = hardwareMap.get(DcMotor.class, "BLWheel");
        DcMotor hangerRotater = hardwareMap.get(DcMotor.class, "HangerRotater");
        //0, 1, 2, 3 CH
        DcMotor linearActuator = hardwareMap.get(DcMotor.class, "LinearActuator");
        // Servos
        //5, CH
        CRServo leftIntakeServo = hardwareMap.get(CRServo.class, "LeftIntakeServo");
        //4, CH
        Servo drone = hardwareMap.get(Servo.class, "Drone");

        // Initialization of Expansion Hub things
        // Motors
        //Expansion Hub
        DcMotor FRWheel = hardwareMap.get(DcMotor.class, "FRWheel");
        DcMotor viperSlide = hardwareMap.get(DcMotor.class, "ViperSlide");
        //0, 1 Exp
        DcMotor BRWheel = hardwareMap.get(DcMotor.class, "BRWheel");
        // Servos
        //5 Exp
        CRServo rightIntakeServo = hardwareMap.get(CRServo.class, "RightIntakeServo");
        Servo viperSlideServo = hardwareMap.get(Servo.class, "ViperSlideServo");
        //4 Exp, 3 Exp, 2 Exp
        Servo wristServo = hardwareMap.get(Servo.class, "WristServo");
        Servo blackClawServo = hardwareMap.get(Servo.class, "Claw1");
        Servo goldClawServo = hardwareMap.get(Servo.class, "Claw2");

        // Set motor directions if needed
        // Flip the right side wheels so that positive seems the same for both sides
        BRWheel.setDirection(DcMotorSimple.Direction.REVERSE);
        FRWheel.setDirection(DcMotorSimple.Direction.REVERSE);

        // Multiplier and flippers
        double factor = 0.25;

        // In-between position for driving
        viperSlideServo.setPosition(0.9);

        waitForStart();
        while (opModeIsActive()) {
            /*
            FULL CONTROL SCHEMATICS:
                gamepad1:
                    left_stick:     drive/strafe
                    right_stick:    rotate

                    left_trigger:   start retract v. slide
                    right_trigger:  start extend v. slide

                    a:              original v. slide wrist position
                    b:              collection v. slide wrist position
                    x:              pixel placement v. slide wrist position
                    y:              launch drone

                    dpad:
                        down:       original v. s. servo position
                        left/right: in-between v. s. servo position
                        up:         scoring v. s. servo position

                gamepad2:
                    left_stick:     hanger turner
                    right_stick:    hanger extend/retract

                    left_bumper:    stop intake wheels
                    right_bumper:   start intake wheels

                    left_trigger:   close claw
                    right_trigger:  open claw
             */

            ////CODE FOR DRIVETRAIN ///
            double drive = -gamepad1.left_stick_x;
            double strafe = gamepad1.left_stick_y;
            double rotate = gamepad1.right_stick_x;
            double frontLeftPower = Range.clip(drive + strafe + rotate, -1.0, 1.0);
            double frontRightPower = Range.clip(drive - strafe - rotate, -1.0, 1.0);
            double backLeftPower = Range.clip(drive - strafe + rotate, -1.0, 1.0);
            double backRightPower = Range.clip(drive + strafe - rotate, -1.0, 1.0);

            FLWheel.setPower(factor * frontLeftPower);
            FRWheel.setPower(factor * frontRightPower);
            BLWheel.setPower(factor * backLeftPower);
            BRWheel.setPower(factor * backRightPower);
            //Updating telemetry
            telemetry.addData("Status", "Running");
            telemetry.addData("Front Left Power", frontLeftPower);
            telemetry.addData("Front Right Power", frontRightPower);
            telemetry.addData("Back Left Power", backLeftPower);
            telemetry.addData("Back Right Power", backRightPower);

            telemetry.update();
            telemetry.update();


            //Servo Wrist Controls
            if (gamepad1.a)
            {
                wristServo.setPosition(0); // og position
            }
            if (gamepad1.b)
            {
                wristServo.setPosition(0.5); // position up for possessing the pixels
            }
            if (gamepad1.x)
            {
                wristServo.setPosition(1); // position on the wat down for placing the pixels
            }
            // Drone Launch!
            if (gamepad1.y)
            {
                drone.setPosition(-0.5);
            }

            //Control of the v. slide Servo
            if (gamepad1.dpad_down)
            {
                viperSlideServo.setPosition(1.2);//home position
            }
            if (gamepad1.dpad_left || gamepad1.dpad_right)
            {
                viperSlideServo.setPosition(0.9); //start to tilt up
            }
            if (gamepad1.dpad_up)
            {
                viperSlideServo.setPosition(0.3);//scoring position
            }

            // Control of the extension of the v. slide
            viperSlide.setPower(-gamepad1.left_trigger);
            viperSlide.setPower(gamepad1.right_trigger);

            ///////////////////////////////////////

            // Intake mill controls (GAMEPAD 2)

            if (gamepad2.left_bumper)
            {
                rightIntakeServo.setPower(0);
                leftIntakeServo.setPower(0);
            }
            if (gamepad2.right_bumper)
            {
                rightIntakeServo.setPower(1);
                leftIntakeServo.setPower(-1);
            }

            // Control of the linear actuator
            linearActuator.setPower(gamepad2.right_stick_y);
            // Control of the Linear Actuator's Turner
            hangerRotater.setPower(gamepad2.left_stick_y);
            // Control claw
            blackClawServo.setPosition(blackClawServo.getPosition() - gamepad2.left_trigger);
            goldClawServo.setPosition(goldClawServo.getPosition() + gamepad2.right_trigger);
        }
    }
}
