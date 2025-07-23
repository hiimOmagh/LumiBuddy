# Calibration Guide

LumiBuddy relies on calibration to convert raw lux readings into accurate PPFD values. The `CalibrationWizardFragment` walks you through recording a correction factor for your device. This document explains how the wizard works, where factors are stored, and how they affect measurements.

## Overview of the Calibration Flow

The calibration wizard measures a known light source and compares the reading with a reference PPFD value. It then calculates a factor so future measurements compensate for sensor bias. The factor is previewed before being saved so you can retry if necessary.

## Using the Calibration Wizard

Open **Settings → Calibration** and follow the on-screen steps. After capturing a sensor reading, enter the reference PPFD from your external meter. Completing the flow stores the factor and notifies you that calibration succeeded.

## Storing Correction Factors

Correction factors are saved in shared preferences under your device ID. They persist across app restarts and are automatically backed up if cloud sync is enabled.

## Applying Factors in Measurements

When you take a measurement, the current factor multiplies the raw lux value to produce PPFD. A zero factor means calibration has not been performed and raw values are shown.

## Why Calibration Matters

Accurate PPFD is critical for recommending lighting schedules and tracking growth over time. Calibrating once per device ensures LumiBuddy's readings are trustworthy.
