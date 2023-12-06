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

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
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

@Autonomous(name="Robot: Auto Drive By Encoder", group="Robot")

public class AutonomousDriveMode extends LinearOpMode {
    //Camera usage initializations//
    private static final boolean USE_WEBCAM = true;
    private TfodProcessor tfod;
    private VisionPortal visionPortal;
    ///////////////////////////////
    /* Declare OpMode members. */
    private DcMotor         leftDriveF   = null;
    private DcMotor         leftDriveB   = null;
    private DcMotor         rightDriveF  = null;
    private DcMotor         rightDriveB  = null;

    private ElapsedTime     runtime = new ElapsedTime();

    // Calculate the COUNTS_PER_INCH for your specific drive train.
    // Go to your motor vendor website to determine your motor's COUNTS_PER_MOTOR_REV
    // For external drive gearing, set DRIVE_GEAR_REDUCTION as needed.
    // For example, use a value of 2.0 for a 12-tooth spur gear driving a 24-tooth spur gear.
    // This is gearing DOWN for less speed and more torque.
    // For gearing UP, use a gear ratio less than 1.0. Note this will affect the direction of wheel rotation.
    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // No External Gearing.
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                                                      (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.6;
    static final double     TURN_SPEED              = 0.5;

    @Override
    public void runOpMode() {
        //initializing camera stuff//
        initTfod();
        telemetry.addData("DS preview on/off", "3 dots, Camera Stream");
        telemetry.addData(">", "Touch Play to start OpMode");

        // Initialize the drive system variables.
        //Front
        leftDriveF  = hardwareMap.get(DcMotor.class, "frontLeft");
        rightDriveF = hardwareMap.get(DcMotor.class, "frontRight");
        //Back
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

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Starting at",  "%7d :%7d",
                          leftDriveF.getCurrentPosition(),
                          rightDriveF.getCurrentPosition(),
                leftDriveB.getCurrentPosition(),
                rightDriveB.getCurrentPosition());
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        //Camera command
        telemetryTfod();
        //
        // Push telemetry to the Driver Station.
        // Step through each leg of the path,
        // Note: Reverse movement is obtained by setting a negative distance (not speed)
        encoderDrive(DRIVE_SPEED,  48,  48, 5.0);  // S1: Forward 47 Inches with 5 Sec timeout
        encoderDrive(TURN_SPEED,   12, -12, 4.0);  // S2: Turn Right 12 Inches with 4 Sec timeout
        encoderDrive(DRIVE_SPEED, -24, -24, 4.0);  // S3: Reverse 24 Inches with 4 Sec timeout

        telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(1000);  // pause to display final telemetry message.
    }

    /*
     *  Method to perform a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the OpMode running.
     */
    public void strafeDriveH (double speed, double Inches, double timeoutS)
    {
        int newFLBRTarget;
        int newFRBLTarget;
        //Write code that will send a command to all 4 motors that allow strafing left/right
        if (Inches < 0) //if going left
        {
            //strafe to the LEFT however many inches
            //FL and BR wheels spin forward, others spin back
            newFLBRTarget = leftDriveF.getCurrentPosition() + (int)(Inches * COUNTS_PER_INCH);
            newFRBLTarget = rightDriveF.getCurrentPosition() - (int)(Inches * COUNTS_PER_INCH);
            leftDriveF.setTargetPosition(newFLBRTarget);
            rightDriveF.setTargetPosition(newFRBLTarget);
            leftDriveB.setTargetPosition(newFRBLTarget);
            rightDriveB.setTargetPosition(newFLBRTarget);
            //set run to position
            leftDriveF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightDriveF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftDriveB.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightDriveB.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            //start motion
            runtime.reset();
            leftDriveF.setPower(Math.abs(speed));
            rightDriveF.setPower(Math.abs(speed));
            leftDriveB.setPower(Math.abs(speed));
            rightDriveB.setPower(Math.abs(speed));
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (leftDriveF.isBusy() && rightDriveF.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Currently at",  " at %7d :%7d",
                        leftDriveF.getCurrentPosition(), rightDriveF.getCurrentPosition());
                telemetry.update();
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
        if (Inches > 0) //if going right
        {
            //strafe to the RIGHT however many inches
            //FR and BL wheels spin forward, others spin back
            newFLBRTarget = leftDriveF.getCurrentPosition() + (int)(Inches * COUNTS_PER_INCH);
            newFRBLTarget = rightDriveF.getCurrentPosition() - (int)(Inches * COUNTS_PER_INCH);
            leftDriveF.setTargetPosition(newFLBRTarget);
            rightDriveF.setTargetPosition(newFRBLTarget);
            leftDriveB.setTargetPosition(newFRBLTarget);
            rightDriveB.setTargetPosition(newFLBRTarget);
            //set run to position
            leftDriveF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightDriveF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftDriveB.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightDriveB.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            //start motion
            runtime.reset();
            leftDriveF.setPower(Math.abs(speed));
            rightDriveF.setPower(Math.abs(speed));
            leftDriveB.setPower(Math.abs(speed));
            rightDriveB.setPower(Math.abs(speed));
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (leftDriveF.isBusy() && rightDriveF.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Currently at",  " at %7d :%7d",
                        leftDriveF.getCurrentPosition(), rightDriveF.getCurrentPosition());
                telemetry.update();
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
    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
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
                   (leftDriveF.isBusy() && rightDriveF.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Running to",  " %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.addData("Currently at",  " at %7d :%7d",
                                            leftDriveF.getCurrentPosition(), rightDriveF.getCurrentPosition());
                telemetry.update();
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
    ///Optical Detection Function Library /////
    private void initTfod() {

        // Create the TensorFlow processor the easy way.
        tfod = TfodProcessor.easyCreateWithDefaults();

        // Create the vision portal the easy way.
        if (USE_WEBCAM) {
            visionPortal = VisionPortal.easyCreateWithDefaults(
                    hardwareMap.get(WebcamName.class, "Webcam 1"), tfod);
        } else {
            visionPortal = VisionPortal.easyCreateWithDefaults(
                    BuiltinCameraDirection.BACK, tfod);
        }

    } //video function
    private void telemetryTfod() {

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
        }   // end for() loop
    } //video function
}

