package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class IntakeServoTesting extends LinearOpMode {

    private CRServo Left, Right, Roller;
    private Servo Servo, Servo2;


    public void runOpMode() throws InterruptedException {
        telemetry.addData("Mode", "waiting");
        telemetry.update();

        //Initialization
        // Servo = hardwareMap.get(Servo.class, "AutoServo");
        Servo = hardwareMap.get(Servo.class, "WristServo");
        Servo2 = hardwareMap.get(Servo.class, "ViperSlideServo");
       Left = hardwareMap.get(CRServo.class, "LeftIntakeServo");
       Right = hardwareMap.get(CRServo.class, "RightIntakeServo");
        Roller = hardwareMap.get(CRServo.class, "Roller");
        waitForStart();


        while (opModeIsActive()) {
            ////CODE///
           Right.setPower(1);
           Left.setPower(-1);
           Roller.setPower(-1);


            if(gamepad1.b)
            {
                Servo2.setPosition(0.6);
                Servo.setPosition(0.53);
            }


            //the direction to go up for the viper slide servo is __________

        }
    }
}
