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

import android.annotation.SuppressLint;
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
public class AutonSpikeOnlyRed extends LinearOpMode {
    //Camera usage initializations//
    /*private static final boolean USE_WEBCAM = true;
    private TfodProcessor tfod;
    private VisionPortal visionPortal;
     */
    /* Declare OpMode members. */
    private DcMotor leftDriveF = null;
    private DcMotor leftDriveB = null;
    private DcMotor rightDriveF = null;
    private DcMotor rightDriveB = null;
    private Servo Auto = null;
    private Servo SpikePixel = null;
    private ElapsedTime runtime = new ElapsedTime();

    static final double COUNTS_PER_MOTOR_REV = 537.7;    // Gobilda Planetary Motor
    static final double DRIVE_GEAR_REDUCTION = 1.0;     // No External Gearing.
    static final double WHEEL_DIAMETER_INCHES = 3.77953;     // For figuring circumference
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double DRIVE_SPEED = 0.4;

    //VIDEO ANALYSIS INITIALIZATION //
    /////////////////////////////////////////////////////////////
    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera

    // TFOD_MODEL_ASSET points to a model file stored in the project Asset location,
    // this is only used for Android Studio when using models in Assets.
    private static final String TFOD_MODEL_ASSET = "model_RedTeamProp.tflite";
    // TFOD_MODEL_FILE points to a model file stored onboard the Robot Controller's storage,
    // this is used when uploading models directly to the RC using the model upload interface.

    private static final String TFOD_MODEL_FILE = "/sdcard/FIRST/tflitemodels/model_RedTeamProp.tflite";
    // Define the labels recognized in the model for TFOD (must be in training order!)
    private static final String[] LABELS = {
            "RedProp",
    };

    /**
     * The variable to store our instance of the TensorFlow Object Detection processor.
     */
    private TfodProcessor tfod;

    /**
     * The variable to store our instance of the vision portal.
     */
    private VisionPortal visionPortal;

    @Override
    public void runOpMode() {

        initTfod(); //TODO: make this return x coord of the object

        // Wait for the DS start button to be touched.
        telemetry.addData("DS preview on/off", "3 dots, Camera Stream");
        telemetry.addData(">", "Touch Play to start OpMode");
        telemetry.update();
        //
        // Initialize the drive system variables.
        //Front
        leftDriveF = hardwareMap.get(DcMotor.class, "frontLeft");
        rightDriveF = hardwareMap.get(DcMotor.class, "frontRight");
        //Back
        leftDriveB = hardwareMap.get(DcMotor.class, "backLeft");
        rightDriveB = hardwareMap.get(DcMotor.class, "backRight");
        //Auto Servo
        Auto = hardwareMap.servo.get("auto");
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


        double xCoordinateValue = telemetryTfod();
        double Heading = 0;
        if (xCoordinateValue > 0 && xCoordinateValue < 200)
        {
            Heading = 1; //This represents LEFT
        }
        if (xCoordinateValue > 250)
        {
            Heading = 2; //This represents CENTER
        }
        else
        {
            Heading = 3; //DEFAULT HEADING IS RIGHT
        }
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
         //TODO: change function to return the x coordinate and what side spikes on
                         //Should probably do this BEFORE the waitforstart and give a telemetry update.
        sleep(500);


        //TODO: this needs to change to being defined as the return value of telemetryTfod

        //write function that lets strafing right x inches happen and put it here.
        encoderDrive(DRIVE_SPEED, 25, 25, 3.0);

        if (Heading == 1) //good experimental val
        {
            //TODO: write code to drive to left spike, drop spike pix
            //blah blah blah at spike mark
            encoderDriveStrafe(DRIVE_SPEED,36, 0, 0.5); //0 is right 1 is left
            encoderDrive(DRIVE_SPEED, 12, 12, 0.5);
            SpikePixel.setPosition(0.5);
            //blah blah blah at the board
            //1. turn left to face board; 2.forward 36 inches; 3. strafe left 18 inches; 4. back up 10 inches?

        }
        if (Heading == 2) //good experimental val
        {
            //TODO: write code to drive to middle spike
            encoderDriveStrafe(DRIVE_SPEED,50, 0, 0.5); //0 is right 1 is left
            SpikePixel.setPosition(0.5);
        }
        else //if (Heading == 3) We will use right as the default statement
        {
            //TODO: write code to drive to right spike
            encoderDriveStrafe(DRIVE_SPEED,36, 0, 0.5); //0 is right 1 is left
            encoderDrive(DRIVE_SPEED, -12, -12, 0.5);
            SpikePixel.setPosition(0.5);
        }
        //write in the value that lets the auton pixel get dropped. 0.5 is a guess.

        sleep(1000);
        //TODO: Write code that drives robot to the CORRECT STACK on the board and place yellow
        //TODO: Write code that drives robot to white stack and run function to pick up
        //TODO: write code that drives robot to board and place two pixels

        // encoderDrive(DRIVE_SPEED,  48,  48, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
        // encoderDrive(TURN_SPEED,   12, -12, 4.0);  // S2: Turn Right 12 Inches with 4 Sec timeout
        // encoderDrive(DRIVE_SPEED, -24, -24, 4.0);  // S3: Reverse 24 Inches with 4 Sec timeout

        // telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(1000);  // pause to display final telemetry message.
    }

    //The below function is slightly modified from FTC's own sample encoder drive for a tank drive
    //This means its only good for rotation and forward/back drive. For strafing, use the new
    //functions below it named encoderStrafeRight or EncoderStrafeLeft
    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the OpMode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = leftDriveF.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            newRightTarget = rightDriveF.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
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
                    (leftDriveF.isBusy() || rightDriveF.isBusy() || leftDriveB.isBusy() || rightDriveB.isBusy())) {
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

            sleep(200);   //  pause after each move.
        }
    }
    public void encoderDriveStrafe(double speed, double InchesStrafe, double direction, double timeoutS) {
        int StrafeFL = 0;
        int StrafeBL = 0;
        int StrafeFR = 0;
        int StrafeBR = 0;

        // Ensure that the OpMode is still active
        if (opModeIsActive())
        {
            // Determine new target position, and pass to motor controller
            if (direction == 0)
            { // strafing right
                StrafeFL = leftDriveF.getCurrentPosition() + (int) (InchesStrafe * COUNTS_PER_INCH);
                StrafeBL = leftDriveB.getCurrentPosition() - (int) (InchesStrafe * COUNTS_PER_INCH);
                StrafeFR = rightDriveF.getCurrentPosition() - (int) (InchesStrafe * COUNTS_PER_INCH);
                StrafeBR = rightDriveF.getCurrentPosition() + (int) (InchesStrafe * COUNTS_PER_INCH);
            }

            if (direction == 1)
            { // strafing left
                StrafeFL = leftDriveF.getCurrentPosition() - (int) (InchesStrafe * COUNTS_PER_INCH);
                StrafeBL = leftDriveB.getCurrentPosition() + (int) (InchesStrafe * COUNTS_PER_INCH);
                StrafeFR = rightDriveF.getCurrentPosition() + (int) (InchesStrafe * COUNTS_PER_INCH);
                StrafeBR = rightDriveF.getCurrentPosition() - (int) (InchesStrafe * COUNTS_PER_INCH);
            }

            leftDriveF.setTargetPosition(StrafeFL);
            rightDriveF.setTargetPosition(StrafeBL);
            leftDriveB.setTargetPosition(StrafeFR);
            rightDriveB.setTargetPosition(StrafeBR);

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
                    (leftDriveF.isBusy() || rightDriveF.isBusy() || leftDriveB.isBusy() || rightDriveB.isBusy())) {
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

            sleep(200);   //  pause after each move.
        }
    }

    ///Function Library /////
    private void DropPixel() {
        //TODO: Write code that will place currently held pixels onto the backboard.
    }

    private void PickUpPixels() {
        //TODO: Write code that will pick up two white pixels from the stack into the robots posession.
    }
    //FUNCTIONS FOR THE VIDEO ANALYSIS BELOW:
    private void initTfod() {

        // Create the TensorFlow processor by using a builder.
        tfod = new TfodProcessor.Builder()

                // With the following lines commented out, the default TfodProcessor Builder
                // will load the default model for the season. To define a custom model to load,
                // choose one of the following:
                //   Use setModelAssetName() if the custom TF Model is built in as an asset (AS only).
                //   Use setModelFileName() if you have downloaded a custom team model to the Robot Controller.
                .setModelAssetName(TFOD_MODEL_ASSET)
                //.setModelFileName(TFOD_MODEL_FILE)

                // The following default settings are available to un-comment and edit as needed to
                // set parameters for custom models.
                .setModelLabels(LABELS)
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

    /**
     * Add telemetry about TensorFlow Object Detection (TFOD) recognitions.
     *
     * @return
     */
    private double telemetryTfod() {
        double ReturnX = 0;

        List<Recognition> currentRecognitions = tfod.getRecognitions();
        telemetry.addData("# Objects Detected", currentRecognitions.size());

        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {
            double x = (recognition.getLeft() + recognition.getRight()) / 2 ;
            double y = (recognition.getTop()  + recognition.getBottom()) / 2 ;

            telemetry.addData(""," ");
            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
            telemetry.addData("- Position", "%.0f / %.0f", x, y);
            telemetry.addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());
            ReturnX = x;
        }  return ReturnX; // This used to be a for loop, but now I make it return the x value after one recognition. Risky? Maybe..
    }   // end method telemetryTfod()
}
