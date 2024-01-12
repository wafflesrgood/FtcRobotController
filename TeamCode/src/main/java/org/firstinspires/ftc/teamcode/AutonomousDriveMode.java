/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;

/*
 * This OpMode illustrates the concept of driving a path based on encoder counts.
 * The code is structured as a LinearOpMode
 *
 * The code REQUIRES that you DO have encoders on the wheels,
 *   otherwise you would use: RobotAutoDriveByTime;
 *
 *  This code ALSO requires that the drive Motors have been configured such that a positive
 *  power command moves them forward, and causes the encoders to count UP.
 *
 *   The desired path in this example is:
 *   - Drive forward for 48 inches
 *   - Spin right for 12 Inches
 *   - Drive Backward for 24 inches
 *   - Stop and close the claw.
 *
 *  The code is written using a method called: encoderDrive(speed, leftInches, rightInches, timeoutS)
 *  that performs the actual movement.
 *  This method assumes that each movement is relative to the last stopping place.
 *  There are other ways to perform encoder based moves, but this method is probably the simplest.
 *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */

@Autonomous

public class AutonomousDriveMode extends LinearOpMode {
    //Camera usage initializations//
    private static final boolean USE_WEBCAM = true;
    private TfodProcessor tfod;
    private VisionPortal visionPortal;
    ///////////////////////////////
    /* Declare OpMode members. */

    private DcMotor leftDriveF   = null;
    private DcMotor leftDriveB   = null;
    private DcMotor rightDriveF  = null;
    private DcMotor rightDriveB  = null;

    private DcMotor ArmMotor = null;
    private Servo ClawServo = null;
    private Servo JointServo = null;
    private DcMotor intake = null;
    private ElapsedTime     runtime = new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV    = 537.7 ;    // GoBilda Planetary Motor
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // No External Gearing.
    static final double     WHEEL_DIAMETER_INCHES   = 3.77953 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                                                      (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.4;
    static final double     TURN_SPEED              = 0.4;

    @Override
    public void runOpMode() {
        //initializing camera stuff//
        initTfod();
        telemetry.addData("DS preview on/off", "3 dots, Camera Stream");
        telemetry.addData(">", "Touch Play to start OpMode");
        //initialize intake motor
        intake = hardwareMap.get(DcMotor.class, "Intake");
        // Initialize the drive system variables.
        // Front
        leftDriveF  = hardwareMap.get(DcMotor.class, "frontLeft");
        rightDriveF = hardwareMap.get(DcMotor.class, "frontRight");
        // Back
        leftDriveB  = hardwareMap.get(DcMotor.class, "backLeft");
        rightDriveB = hardwareMap.get(DcMotor.class, "backRight");
        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        leftDriveF.setDirection(DcMotor.Direction.FORWARD);
        rightDriveF.setDirection(DcMotor.Direction.REVERSE);
        leftDriveB.setDirection(DcMotor.Direction.FORWARD);
        rightDriveB.setDirection(DcMotor.Direction.REVERSE);

        leftDriveF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDriveF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftDriveB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDriveB.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftDriveF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDriveF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftDriveB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDriveB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        /////////////////////////////////////////////////////////
        //Arm Motor Initialization////
        ArmMotor = hardwareMap.get(DcMotor.class, "ArmMotor");
        ArmMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        JointServo = hardwareMap.get(Servo.class, "JointServo");
        ClawServo = hardwareMap.get(Servo.class, "ClawServo");

        // Send telemetry message to indicate successful Encoder reset

        telemetry.addData("Arm Position", "%7d :%7d", ArmMotor.getCurrentPosition());
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        ArmMotor.setTargetPosition(5);
        ArmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        ArmMotor.setPower(0.4);
        ClawServo.setPosition(0.75);
        JointServo.setPosition(0.78);
        waitForStart();
        //Camera command that stores XCoordinate of pixel

        double xCoordinateValue = telemetryTfod();
        sleep(500);
        visionPortal.close();
        //

        //write commands that will grab and lift pixel
        ClawServo.setPosition(0.5); //Grips pixel
        JointServo.setPosition(0.9); //prepares wrist to lift
        //
        //
        //
        //
        //
        //move forward 25 inches
        encoderDrive(DRIVE_SPEED,  25,  25, 3.0);
        ////

        if (xCoordinateValue != 0.0 && xCoordinateValue < 200) //good experimental val
        {
            //drive to middle spike
            //move forward 8 more inches
            encoderDrive(DRIVE_SPEED,  8,  8, 2.0);
            //drop the pixel gently
            DropPixel();
            //rotate to face back towards start to grab pixel!

        }
        if(xCoordinateValue > 200)//good experimental val
        {
            //drive to right spike
            //
            //rotate 90 degrees to the right
            encoderDrive(TURN_SPEED,   16, -16, 2.0);
            //move forward 10 inches
            encoderDrive(DRIVE_SPEED,  8,  8, 2.0);
            //drop the pixel
            DropPixel();
            //rotate to face back towards start to grab pixel!
        }
        else
        {
            //drive to left spike
            //
            //rotate 90 degrees to the left
            encoderDrive(TURN_SPEED,   -16, 16, 2.0);
            //move forward 10 inches
            encoderDrive(DRIVE_SPEED,  7,  7, 2.0);
            //drop the pixel
            DropPixel();
            //rotate to face back towards start to grab pixel!
        }

        sleep(1000);
       // encoderDrive(DRIVE_SPEED,  48,  48, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
       // encoderDrive(TURN_SPEED,   12, -12, 4.0);  // S2: Turn Right 12 Inches with 4 Sec timeout
       // encoderDrive(DRIVE_SPEED, -24, -24, 4.0);  // S3: Reverse 24 Inches with 4 Sec timeout

       // telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(1000);  // pause to display final telemetry message.
    }

    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS)
    {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the OpMode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = leftDriveF.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightTarget = rightDriveF.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            leftDriveF.setTargetPosition(newLeftTarget);
            rightDriveF.setTargetPosition(newRightTarget);
            leftDriveB.setTargetPosition(newLeftTarget);
            rightDriveB.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            leftDriveF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightDriveF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftDriveB.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightDriveB.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            leftDriveF.setPower(Math.abs(speed));
            rightDriveF.setPower(Math.abs(speed));
            leftDriveB.setPower(Math.abs(speed));
            rightDriveB.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                   (runtime.seconds() < timeoutS) &&
                   (leftDriveF.isBusy() || rightDriveF.isBusy() || leftDriveB.isBusy() || rightDriveB.isBusy()))
            {
                //imagine i put a telemetry statement here
                telemetry.addData("Top Left Motor Position", "%7d :%7d", leftDriveF.getCurrentPosition());
                telemetry.addData("Top Right Motor Position", "%7d :%7d", rightDriveF.getCurrentPosition());
                telemetry.addData("Back Left Motor Position", "%7d :%7d", leftDriveB.getCurrentPosition());
                telemetry.addData("Back Right Motor Position", "%7d :%7d", rightDriveB.getCurrentPosition());
            }

            // Stop all motion;
            leftDriveF.setPower(0);
            rightDriveF.setPower(0);
            leftDriveB.setPower(0);
            rightDriveB.setPower(0);

            // Turn off RUN_TO_POSITION
            leftDriveF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightDriveF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftDriveB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightDriveB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            sleep(200);   // optional pause after each move.
        }
    }

    public void strafeDrive(
            double speed)
    {

    }

    // Optical Detection Function Library
    private void DropPixel()
    {
        JointServo.setPosition(0.78); //wrist down
        ClawServo.setPosition(0.75); //let go of pixel
        //Intake Spit Out Mode (SLOW!)
        intake.setPower(-0.25);
        //Drive back a few inches to spit out
        encoderDrive(DRIVE_SPEED,  -8,  -8, 3.0);
        intake.setPower(0.0); //        turn off intake mill
        ClawServo.setPosition(0.5); //  grip so claw doesn't hit side when wrist moves next
        JointServo.setPosition(0.9); // wrist back up

    }
    private void initTfod() {

        // Create the TensorFlow processor by using a builder.
        tfod = new TfodProcessor.Builder()

                // With the following lines commented out, the default TfodProcessor Builder
                // will load the default model for the season. To define a custom model to load,
                // choose one of the following:
                // Use setModelAssetName() if the custom TF Model is built in as an asset (AS only).
                // Use setModelFileName() if you have downloaded a custom team model to the Robot Controller.
                //.setModelAssetName(TFOD_MODEL_ASSET)
                //.setModelFileName(TFOD_MODEL_FILE)

                // The following default settings are available to un-comment and edit as needed to
                // set parameters for custom models.
                //.setModelLabels(LABELS)
                //.setIsModelTensorFlow2(true)
                //.setIsModelQuantized(true)
                //.setModelInputSize(300)
                //.setModelAspectRatio(16.0 / 9.0)

                .build();

        // Create the vision portal by using a builder.
        VisionPortal.Builder builder = new VisionPortal.Builder();

        // Set the camera (webcam vs. built-in RC phone camera).
        if (USE_WEBCAM) {
            builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK);
        }

        // Choose a camera resolution. Not all cameras support all resolutions.
        builder.setCameraResolution(new Size(640, 480));

        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
        builder.enableLiveView(true);

        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
        builder.setStreamFormat(VisionPortal.StreamFormat.YUY2);

        // Choose whether or not LiveView stops if no processors are enabled.
        // If set "true", monitor shows solid orange screen if no processors enabled.
        // If set "false", monitor shows camera view without annotations.
        builder.setAutoStopLiveView(false);

        // Set and enable the processor.
        builder.addProcessor(tfod);

        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();

        // Set confidence threshold for TFOD recognitions, at any time.
        tfod.setMinResultConfidence(0.50f);

        // Disable or re-enable the TFOD processor at any time.
        //visionPortal.setProcessorEnabled(tfod, true);

    }   // end method initTfod()
    private double telemetryTfod() {
        double xCoordinate = 0.0;
        List<Recognition> currentRecognitions = tfod.getRecognitions();
        telemetry.addData("# Objects Detected", currentRecognitions.size());

        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {
            double x = (recognition.getLeft() + recognition.getRight()) / 2;
            double y = (recognition.getTop() + recognition.getBottom()) / 2;

            telemetry.addData("", " ");
            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
            telemetry.addData("- Position", "%.0f / %.0f", x, y);
            telemetry.addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());
            xCoordinate = x;
        }   // end for() loop
        return xCoordinate;
    } //video function returns x cord
}

