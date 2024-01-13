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
    // Control Hub
    private DcMotor FLWheel, BLWheel, HangerRotater, LinearActuator; //0, 1, 2, 3 CH
    private CRServo LeftIntakeServo; //5, CH
    private CRServo Roller; //TBD
    private Servo Drone; //4, CH

    //Expansion Hub
    private DcMotor FRWheel, ViperSlide, VSAdjuster, BRWheel; //0, 1 Exp
    private CRServo RightIntakeServo; //5 Exp
    private Servo AutonomousServo, ViperSlideServo, WristServo; //4 Exp, 3 Exp, 2 Exp
    //webcam is plugged into the control hub.


    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Waiting");
        telemetry.update();
        // Initialization of Control Hub things
        // Motors
        FLWheel = hardwareMap.get(DcMotor.class, "FLWheel");
        BLWheel = hardwareMap.get(DcMotor.class, "BLWheel");
        HangerRotater = hardwareMap.get(DcMotor.class, "HangerRotater");
        LinearActuator = hardwareMap.get(DcMotor.class, "LinearActuator");
        // Servos
        LeftIntakeServo = hardwareMap.get(CRServo.class, "LeftIntakeServo");
        Drone = hardwareMap.get(Servo.class, "Drone");
        Roller = hardwareMap.get(CRServo.class, "Roller");

        // Initialization of Expansion Hub things
        // Motors
        FRWheel = hardwareMap.get(DcMotor.class, "FRWheel");
        ViperSlide = hardwareMap.get(DcMotor.class, "ViperSlide");
        VSAdjuster = hardwareMap.get(DcMotor.class, "ViperRotator");
        BRWheel = hardwareMap.get(DcMotor.class, "BRWheel");
        HangerRotater = hardwareMap.get(DcMotor.class, "HangerRotater");
        // Servos
        RightIntakeServo = hardwareMap.get(CRServo.class, "RightIntakeServo");
        AutonomousServo = hardwareMap.get(Servo.class, "AutonomousServo");
        ViperSlideServo = hardwareMap.get(Servo.class, "ViperSlideServo");
        WristServo = hardwareMap.get(Servo.class, "WristServo");

        // Set motor directions if needed
        // Flip the right side wheels so that positive seems the same for both sides
        BRWheel.setDirection(DcMotorSimple.Direction.REVERSE);
        FRWheel.setDirection(DcMotorSimple.Direction.REVERSE);

        // Multiplier and flippers
        double factor = 0.25;


        waitForStart();
        while (opModeIsActive()) {
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
            /*
            FULL CONTROL SCHEMATICS:

                gamepad1:
                    left_stick:     drive/strafe
                    right_stick:    rotate

                    left_bumper:    stop intake wheels
                    right_bumper:   start intake wheels

                    a:              original v. slide wrist position
                    b:              collection v. slide wrist position
                    x:              pixel placement v. slide wrist position

                gamepad2:
                    left_stick:     hanger turner
                    right_stick:    hanger extend/retract

                    left_bumper:    start pulling in string for v. slide adjuster
                    right_bumper:   start releasing string for v. slide adjuster
                    back:           stop v. slide adjuster

                    left_trigger:   retract v. slide
                    right_trigger:  extend v. slide

                    a: box intake start
                    b: box intake stop
                    x: launch drone

                    dpad:
                        down:       original v. s. servo position
                        left/right: inbetween v. s. servo position
                        up:         scoring v. s. servo position
             */

            // Intake mill controls (GAMEPAD 1)

            if(gamepad1.right_bumper)
            {
                RightIntakeServo.setPower(1);
                LeftIntakeServo.setPower(-1);
            }
            if(gamepad1.left_bumper)
            {
                RightIntakeServo.setPower(0);
                LeftIntakeServo.setPower(0);
            }

            //Servo Wrist Controls
            if(gamepad1.a)
            {
                WristServo.setPosition(0); // og position
            }
            if (gamepad1.b)
            {
                WristServo.setPosition(0.5); // position up for possessing the pixels
            }
            if(gamepad1.x)
            {
                WristServo.setPosition(1); // position on the wat down for placing the pixels
            }

            ///////////////////////////////////

            // Control of the Viper Slide Adjuster
            if (gamepad2.right_bumper)
            {
                VSAdjuster.setPower(0.5);
            }
            if (gamepad2.left_bumper)
            {
                VSAdjuster.setPower(-0.5);
            }
            if (gamepad2.back)
            {
                VSAdjuster.setPower(0);
            }

            // Control of the extension of the viperslide
            ViperSlide.setPower(0.5 * gamepad2.right_trigger);
            ViperSlide.setPower(0.5 * -gamepad2.left_trigger);

            ///////////////////////////////////////

            //Control of the Viperslide Servo
            if (gamepad2.dpad_down)
            {
                ViperSlideServo.setPosition(0);//home position
            }
            if (gamepad2.dpad_left || gamepad2.dpad_right)
            {
                ViperSlideServo.setPosition(0.5); //start to tilt up
            }
            if (gamepad2.dpad_up)
            {
                ViperSlideServo.setPosition(1);//scoring position
            }
            /////////////////
            //control of the linear actuator

            LinearActuator.setPower(gamepad2.right_stick_y);
            //Control of the Linear Actuator's Turner
            HangerRotater.setPower(gamepad2.left_stick_y);
            //direction of the wheel on the box
            if(gamepad2.a)
            {
                Roller.setPower(1);
            }
            if(gamepad2.b)
            {
                Roller.setPower(-1);
            }
            //Drone Launch! (Gamepad2)
            if (gamepad2.x)
            {
                Drone.setPosition(-0.5);
            }
        }
    }
}
