package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp

public class TeleopTwoDrivers extends LinearOpMode {
    private DcMotor FLWheel, BLWheel, HangerRotater, LinearActuator; //0, 1, 2, 3 CH
    private CRServo LeftIntakeServo; //5, CH
    private CRServo Roller; //TBD
    private Servo Drone; //4, CH

    //Expansion Hub
    private DcMotor FRWheel, ViperSlide, BRWheel; //0, 1 Exp
    private CRServo RightIntakeServo; //5 Exp
    private Servo AutonomousServo, ViperSlideServo, WristServo; //4 Exp, 3 Exp, 2 Exp
    //webcam is plugged into the control hub.


    public void runOpMode() throws InterruptedException {
        telemetry.addData("Mode", "waiting");
        telemetry.update();
        //Initialization of Control Hub things
        //motors
        FLWheel = hardwareMap.get(DcMotor.class, "FLWheel");
        BLWheel = hardwareMap.get(DcMotor.class, "BLWheel");
        HangerRotater = hardwareMap.get(DcMotor.class, "HangerRotater");
        LinearActuator = hardwareMap.get(DcMotor.class, "LinearActuator");
        //servos
        LeftIntakeServo = hardwareMap.get(CRServo.class, "LeftIntakeServo");
        Drone = hardwareMap.get(Servo.class, "Drone");
        Roller = hardwareMap.get(CRServo.class, "Roller");

        //Initialization of Expansion Hub things
        //motors
        FRWheel = hardwareMap.get(DcMotor.class, "FRWheel");
        ViperSlide = hardwareMap.get(DcMotor.class, "ViperSlide");
        BRWheel = hardwareMap.get(DcMotor.class, "BRWheel");
        HangerRotater = hardwareMap.get(DcMotor.class, "HangerRotater");
        //servos
        RightIntakeServo = hardwareMap.get(CRServo.class, "RightIntakeServo");
        //AutonomousServo = hardwareMap.get(Servo.class, "AutonomousServo");
        ViperSlideServo = hardwareMap.get(Servo.class, "ViperSlideServo");
        WristServo = hardwareMap.get(Servo.class, "WristServo");

        // Set motor directions if needed
        //flip the right side wheels so that positive seems the same for both sides
        BRWheel.setDirection(DcMotorSimple.Direction.REVERSE);
        FRWheel.setDirection(DcMotorSimple.Direction.REVERSE);

        //multiplier and flippers
        double multiplier = 0.25;


        waitForStart();
        while (opModeIsActive()) {
            ////CODE FOR DRIVETRAIN ///
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;
            double frontLeftPower  = y - x + rx;
            //Range.clip(drive + strafe + rotate, -1.0, 1.0);
            double frontRightPower =  y - x - rx;
            //Range.clip(drive - strafe - rotate, -1.0, 1.0);
            double backLeftPower = y + x + rx;
            //Range.clip(drive - strafe + rotate, -1.0, 1.0);
            double backRightPower = y + x - rx;
            //Range.clip(drive + strafe - rotate, -1.0, 1.0);
            FLWheel.setPower(multiplier * frontLeftPower);
            FRWheel.setPower(multiplier * frontRightPower);
            BLWheel.setPower(multiplier * backLeftPower);
            BRWheel.setPower(multiplier * backRightPower);
            //Updating telemetry
            telemetry.addData("Status", "Running");
            telemetry.addData("Front Left Power", frontLeftPower);
            telemetry.addData("Front Right Power", frontRightPower);
            telemetry.addData("Back Left Power", backLeftPower);
            telemetry.addData("Back Right Power", backRightPower);

            telemetry.update();
            telemetry.update();
            //

            //Intake mill controls (GAMEPAD 1)

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
            /////////////////////////////////////

            //Servo Wrist Controls
            if(gamepad1.a)
            {
               // WristServo.setPosition(0); //og position
            }
            if (gamepad1.b)
            {
                //WristServo.setPosition(0.5); //position up for possesing the pixels
            }
            if(gamepad1.x)
            {
               // WristServo.setPosition(1); //position on the wat down for placing the pixels
            }

            ///////////////////////////////////

            //Control of the extension of the viperslide
            if(gamepad2.right_bumper)
            {
                //ViperSlide.setPower(0.5);
            }
            if(gamepad2.left_bumper)
            {
                //ViperSlide.setPower(-0.5);
            }
            if(gamepad2.dpad_right)
            {
                //ViperSlide.setPower(0);
            }

            ///////////////////////////////////////

            //Control of the Viperslide Servo
            if(gamepad2.dpad_down)
            {
                //ViperSlideServo.setPosition(0);//home position
            }
            if(gamepad2.dpad_left)
            {
                //ViperSlideServo.setPosition(0.5); //start to tilt up
            }
            if(gamepad2.dpad_up)
            {
                //ViperSlideServo.setPosition(1);//scoring position
            }
            /////////////////
            //control of the linear actuator
            LinearActuator.setPower(gamepad2.right_stick_y);
            //Control of the Linear Actuator's Turner
            HangerRotater.setPower(gamepad2.left_stick_y);
            //direction of the wheel on the box
            if(gamepad2.a)
            {
                //Roller.setPower(1);
            }
            if(gamepad2.b)
            {
                //Roller.setPower(-1);
            }
            //Drone Launch! (Gamepad2)
            if (gamepad1.x)
            {
                //Drone.setPosition(-0.5);
            }
        }
    }
}
