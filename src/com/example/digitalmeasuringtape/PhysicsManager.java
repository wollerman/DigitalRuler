package com.example.digitalmeasuringtape;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.util.FloatMath;

public class PhysicsManager {

	MainActivity main;
	SharedPreferences settings;

	public PhysicsManager(MainActivity main) {
		this.main = main;
		settings = main.sPrefs;
	}

	public double Distance(ArrayList<Float>... args) 
	{
		//args[0] is xData
		//args[1] is yData
		//args[2] is zData
		//args[last] is tData
		//^is that way because args is of variable length, but tData is always last element
		
		double distance;
		
		if(!settings.getBoolean("MeasureY", false))
		{
			//only measured X-dimension
			if(settings.getBoolean("Eulers", false))
			{
				distance = EulersX(args[0], args[1]);
			}else if (settings.getBoolean("ImprovedEulers", true))
			{
				distance = Improved_EulersX(args[0], args[1]);
			}else 
			{
				distance = SimpsonsX(args[0], args[1]);
			}
		}else if (!settings.getBoolean("MeasureZ", false))
		{
			// measured X-dimension and Y-dimension
			if(settings.getBoolean("Eulers", false))
			{
				distance = EulersXY(args[0], args[1], args[2]);
			}else if (settings.getBoolean("ImprovedEulers", true))
			{
				distance = Improved_EulersXY(args[0], args[1], args[2]);
			}else 
			{
				distance = SimpsonsXY(args[0], args[1], args[2]);
			}
		}else
		{
			// measured X-dimension, Y-dimension, and Z-dimension
			if(settings.getBoolean("Eulers", false))
			{
				distance = EulersXYZ(args[0], args[1], args[2], args[3]);
			}else if (settings.getBoolean("ImprovedEulers", true))
			{
				distance = Improved_EulersXYZ(args[0], args[1], args[2], args[3]);
			}else 
			{
				distance = SimpsonsXYZ(args[0], args[1], args[2], args[3]);
			}
			
		}
		
		System.out.println("Just calculated distance of: " + distance);
		char c = 'x';
		for (ArrayList<Float> i_accel : args)
			if(i_accel != args[args.length-1] ) System.out.println(c++ + ": " + i_accel);
		
		System.out.println("t: " + args[args.length-1]);
		return distance;
	}

	public double EulersX(ArrayList<Float> x_accel, ArrayList<Float> t) {
		if (t == null)
			return -1;

		System.out.println("Entering Eulers Distance method");
		System.out.println("x: " + x_accel);
		System.out.println("t: " + t);

		// This is the Euler's method.
		ArrayList<Float> dx_veloc = new ArrayList<Float>();
		ArrayList<Float> x_veloc = new ArrayList<Float>();
		x_veloc.add(0f);

		// compose velocity
		final int STEPS = t.size();
		float dt;
		System.out.println("Composing Velocity from Acceleration...\n");
		for (int i = 0; i < STEPS - 1; i++) {
			// x'_i = x''_(i-1) * dt
			// y'_i = y''_(i-1) * dt
			dt = t.get(i + 1) - t.get(i);
			dx_veloc.add(x_accel.get(i) * dt);
			System.out.println("Step: " + i + "\ndt: " + dt + "\n\tv_x:"
					+ dx_veloc.get(i));
		}
		float temp = 0f;
		for (float d : dx_veloc) {
			temp += d;
			x_veloc.add(temp);
		}

		ArrayList<Float> dx_disp = new ArrayList<Float>();
		ArrayList<Float> x_disp = new ArrayList<Float>();
		x_disp.add(0f);

		// compose displacement
		System.out.println("Composing Displacement from Velocity...\n");
		for (int i = 0; i < STEPS - 1; i++) {
			// x_i = x'_(i-1) * dt
			dt = t.get(i + 1) - t.get(i);
			dx_disp.add(x_veloc.get(i) * dt);

			System.out.println("Step: " + i + "\ndt: " + dt + "\n\td_x:"
					+ dx_disp.get(i));
		}

		// vector addition, constructing R
		System.out.println("Composing R...\n");
		float r = 0; // [x]
		for (int i = 0; i < STEPS - 1; i++) {
			r += dx_disp.get(i);
			System.out.println("Step: " + i + "\n\tr_x: " + r);
		}

		// Distance formula, constructing D
		// D = sqrt(X^2)
		return r;
	}

	public double EulersXY(ArrayList<Float> x_accel, ArrayList<Float> y_accel,
			ArrayList<Float> t) {

		if (t == null)
			return -1;

		System.out.println("Entering Eulers Distance method");
		System.out.println("x: " + x_accel);
		System.out.println("y: " + y_accel);
		System.out.println("t: " + t);

		// This is the Euler's method.
		final ArrayList<Float> dx_veloc = new ArrayList<Float>();
		final ArrayList<Float> dy_veloc = new ArrayList<Float>();

		final ArrayList<Float> x_veloc = new ArrayList<Float>();
		x_veloc.add(0f);
		final ArrayList<Float> y_veloc = new ArrayList<Float>();
		y_veloc.add(0f);

		// compose velocity
		final int STEPS = t.size();
		float dt;
		System.out.println("Composing Velocity from Acceleration...\n");
		for (int i = 0; i < STEPS - 1; i++) {
			// x'_i = x''_(i-1) * dt
			// y'_i = y''_(i-1) * dt
			dt = t.get(i + 1) - t.get(i);
			dx_veloc.add(x_accel.get(i) * dt);
			dy_veloc.add(y_accel.get(i) * dt);
			System.out.println("Step: " + i + "\ndt: " + dt + "\n\tv_x:"
					+ dx_veloc.get(i) + "\n\tv_y: " + dy_veloc.get(i));
		}
		float temp = 0f;
		for (float d : dx_veloc) {
			temp += d;
			x_veloc.add(temp);
		}

		temp = 0f;
		for (float d : dy_veloc) {
			temp += d;
			y_veloc.add(temp);
		}

		final ArrayList<Float> dx_disp = new ArrayList<Float>();
		final ArrayList<Float> dy_disp = new ArrayList<Float>();

		final ArrayList<Float> x_disp = new ArrayList<Float>();
		x_disp.add(0f);
		final ArrayList<Float> y_disp = new ArrayList<Float>();
		y_disp.add(0f);

		// compose displacement
		System.out.println("Composing Displacement from Velocity...\n");
		for (int i = 0; i < STEPS - 1; i++) {
			// x_i = x'_(i-1) * dt
			// y_i = y'_(i-1) * dt
			dt = t.get(i + 1) - t.get(i);
			dx_disp.add(x_veloc.get(i) * dt);
			dy_disp.add(y_veloc.get(i) * dt);

			System.out.println("Step: " + i + "\ndt: " + dt + "\n\td_x:"
					+ dx_disp.get(i) + "\n\td_y: " + dy_disp.get(i));
		}

		// compose total displacement
		float distance = 0;
		if (settings.getBoolean("PathMode", false)) {
			// vector addition, constructing R
			System.out.println("Composing R...\n");
			float rx = 0; // *
			float ry = 0; // *[x, y] *//

			for (int i = 0; i < STEPS - 1; i++) {
				rx += dx_disp.get(i);
				ry += dy_disp.get(i);
				System.out.println("Step: " + i + "\n\tr_x: " + rx
						+ "\n\tr_y: " + ry);
			}

			// Distance formula, constructing D
			// D = sqrt(X^2 + Y^2 )
			return Math.sqrt(rx * rx + ry * ry);
		}

		else {
			// sum up individual distances, constructing D
			for (int i = 0; i < STEPS - 1; i++) {
				// dD = sqrt( dx^2 + dy^2 + dz^2 )
				distance += Math.sqrt(Math.pow(dx_disp.get(i), 2)
						+ Math.pow(dy_disp.get(i), 2));
			}
			return distance;
		}
	}

	public double EulersXYZ(ArrayList<Float> x_accel, ArrayList<Float> y_accel,
			ArrayList<Float> z_accel, ArrayList<Float> t) {

		if (t == null)
			return -1;

		System.out.println("Entering Eulers Distance method");
		System.out.println("x: " + x_accel);
		System.out.println("y: " + y_accel);
		System.out.println("z: " + z_accel);
		System.out.println("t: " + t);

		// This is the Euler's method.
		final ArrayList<Float> dx_veloc = new ArrayList<Float>();
		final ArrayList<Float> dy_veloc = new ArrayList<Float>();
		final ArrayList<Float> dz_veloc = new ArrayList<Float>();

		final ArrayList<Float> x_veloc = new ArrayList<Float>();
		x_veloc.add(0f);
		final ArrayList<Float> y_veloc = new ArrayList<Float>();
		y_veloc.add(0f);
		final ArrayList<Float> z_veloc = new ArrayList<Float>();
		z_veloc.add(0f);

		// compose velocity
		final int STEPS = t.size();
		float dt;
		System.out.println("Composing Velocity from Acceleration...\n");
		for (int i = 0; i < STEPS - 1; i++) {
			// x'_i = x''_(i-1) * dt
			// y'_i = y''_(i-1) * dt
			// z'_i = z''_(i-1) * dt
			dt = t.get(i + 1) - t.get(i);
			dx_veloc.add(x_accel.get(i) * dt);
			dy_veloc.add(y_accel.get(i) * dt);
			dz_veloc.add(z_accel.get(i) * dt);
			System.out.println("Step: " + i + "\ndt: " + dt + "\n\tv_x:"
					+ dx_veloc.get(i) + "\n\tv_y: " + dy_veloc.get(i)
					+ "\n\tv_z: " + dz_veloc.get(i));
		}
		float temp = 0f;
		for (float d : dx_veloc) {
			temp += d;
			x_veloc.add(temp);
		}

		temp = 0f;
		for (float d : dy_veloc) {
			temp += d;
			y_veloc.add(temp);
		}

		temp = 0f;
		for (float d : dz_veloc) {
			temp += d;
			z_veloc.add(temp);
		}

		final ArrayList<Float> dx_disp = new ArrayList<Float>();
		final ArrayList<Float> dy_disp = new ArrayList<Float>();
		final ArrayList<Float> dz_disp = new ArrayList<Float>();

		final ArrayList<Float> x_disp = new ArrayList<Float>();
		x_disp.add(0f);
		final ArrayList<Float> y_disp = new ArrayList<Float>();
		y_disp.add(0f);
		final ArrayList<Float> z_disp = new ArrayList<Float>();
		z_disp.add(0f);

		// compose displacement
		System.out.println("Composing Displacement from Velocity...\n");
		for (int i = 0; i < STEPS - 1; i++) {
			// x_i = x'_(i-1) * dt
			// y_i = y'_(i-1) * dt
			dt = t.get(i + 1) - t.get(i);
			dx_disp.add(x_veloc.get(i) * dt);
			dy_disp.add(y_veloc.get(i) * dt);
			dz_disp.add(z_veloc.get(i) * dt);

			System.out.println("Step: " + i + "\ndt: " + dt + "\n\td_x: "
					+ dx_disp.get(i) + "\n\td_y: " + dy_disp.get(i)
					+ "\n\td_z: " + dz_disp.get(i));
		}

		// compose total displacement
		float distance = 0;
		if (settings.getBoolean("PathMode", false)) {
			// vector addition, constructing R
			System.out.println("Composing R...\n");
			float rx = 0; // *
			float ry = 0; // *[x, y, z] *//
			float rz = 0; // *
			for (int i = 0; i < STEPS - 1; i++) {
				rx += dx_disp.get(i);
				ry += dy_disp.get(i);
				rz += dz_disp.get(i);
				System.out.println("Step: " + i + "\n\tr_x: " + rx
						+ "\n\tr_y: " + ry + "\n\tr_z: " + rz);
			}

			// Distance formula, constructing D
			// D = sqrt(X^2 + Y^2 + Z^2)
			return Math.sqrt(rx * rx + ry * ry + rz * rz);
		}

		else {
			// sum up individual distances, constructing D
			for (int i = 0; i < STEPS - 1; i++) {
				// dD = sqrt( dx^2 + dy^2 + dz^2 )
				distance += Math.sqrt(Math.pow(dx_disp.get(i), 2)
						+ Math.pow(dy_disp.get(i), 2)
						+ Math.pow(dz_disp.get(i), 2));
			}
			return distance;
		}
	}

	public double Improved_EulersX(ArrayList<Float> x_accel, ArrayList<Float> t) {
		if (t == null)
			return -1;
		System.out.println("Entering Improved Eulers Distance method");

		ArrayList<Float> dx_veloc = new ArrayList<Float>();
		ArrayList<Float> x_veloc = new ArrayList<Float>();
		x_veloc.add(0f);

		int STEPS = x_accel.size();
		float dt;
		for (int i = 0; i < STEPS - 1; i++) {
			// dx'_i = dx''_(i-1) * dt + (.5) * dt * (dx''(i) - dx''(i-1))

			dt = t.get(i + 1) - t.get(i);
			dx_veloc.add(x_accel.get(i) * dt + (.5f) * dt
					* (x_accel.get(i + 1) - x_accel.get(i)));

			System.out.println("Step: " + i + "\ndt: " + dt + "\n\tv_x: "
					+ dx_veloc.get(i));
		}

		// Sum up all delta values
		float temp = 0f;
		for (float d : dx_veloc) {
			temp += d;
			x_veloc.add(temp);
		}

		ArrayList<Float> dx_disp = new ArrayList<Float>();

		ArrayList<Float> x_disp = new ArrayList<Float>();
		x_disp.add(0f);

		// compose displacement
		STEPS = x_veloc.size();
		System.out.println("Composing Displacement from Velocity...\n");
		for (int i = 0; i < STEPS - 1; i++) {
			// dx_i = dx'_(i-1) * dt + (.5) * dt * (dx'(i) - dx'(i-1))

			dt = t.get(i + 1) - t.get(i);
			dx_disp.add(x_veloc.get(i) * dt + (.5f) * dt
					* (x_veloc.get(i + 1) - x_veloc.get(i)));

			System.out.println("Step: " + i + "\ndt: " + dt + "\n\td_x: "
					+ dx_disp.get(i));
		}

		// compose total displacement
		// vector addition, constructing R
		System.out.println("Composing R...\n");
		float rx = 0; // [x]

		for (int i = 0; i < STEPS - 1; i++) {
			rx += dx_disp.get(i);
			System.out.println("Step: " + i + "\n\tr_x: " + rx);
		}

		// Distance formula, constructing D
		// D = sqrt(X^2)
		return rx;

	}

	public double Improved_EulersXY(ArrayList<Float> x_accel,
			ArrayList<Float> y_accel, ArrayList<Float> t) {
		if (t == null)
			return -1;

		System.out.println("Entering Improved Eulers Distance method");

		ArrayList<Float> dx_veloc = new ArrayList<Float>();
		ArrayList<Float> dy_veloc = new ArrayList<Float>();

		ArrayList<Float> x_veloc = new ArrayList<Float>();
		x_veloc.add(0f);
		ArrayList<Float> y_veloc = new ArrayList<Float>();
		y_veloc.add(0f);

		int STEPS = x_accel.size();
		float dt;

		for (int i = 0; i < STEPS - 1; i++) {
			// dx'_i = dx''_(i-1) * dt + (.5) * dt * (dx''(i) - dx''(i-1))
			// dy'_i = dy''_(i-1) * dt + (.5) * dt * (dy''(i) - dy''(i-1))

			dt = t.get(i + 1) - t.get(i);
			dx_veloc.add(x_accel.get(i) * dt + (.5f) * dt
					* (x_accel.get(i + 1) - x_accel.get(i)));
			dy_veloc.add(y_accel.get(i) * dt + (.5f) * dt
					* (y_accel.get(i + 1) - y_accel.get(i)));

			System.out.println("Step: " + i + "\ndt: " + dt + "\n\tv_x: "
					+ dx_veloc.get(i) + "\n\tv_y: " + dy_veloc.get(i));
		}

		// Sum up all delta values
		float temp = 0f;
		for (float d : dx_veloc) {
			temp += d;
			x_veloc.add(temp);
		}

		temp = 0;
		for (float d : dy_veloc) {
			temp += d;
			y_veloc.add(temp);
		}

		ArrayList<Float> dx_disp = new ArrayList<Float>();
		ArrayList<Float> dy_disp = new ArrayList<Float>();

		ArrayList<Float> x_disp = new ArrayList<Float>();
		x_disp.add(0f);
		ArrayList<Float> y_disp = new ArrayList<Float>();
		y_disp.add(0f);

		// compose displacement
		STEPS = x_veloc.size();
		System.out.println("Composing Displacement from Velocity...\n");
		for (int i = 0; i < STEPS - 1; i++) {
			// dx_i = dx'_(i-1) * dt + (.5) * dt * (dx'(i) - dx'(i-1))
			// dy_i = dx'_(i-1) * dt + (.5) * dt * (dy'(i) - dy'(i-1))
			dt = t.get(i + 1) - t.get(i);
			dx_disp.add(x_veloc.get(i) * dt + (.5f) * dt
					* (x_veloc.get(i + 1) - x_veloc.get(i)));
			dy_disp.add(y_veloc.get(i) * dt + (.5f) * dt
					* (y_veloc.get(i + 1) - y_veloc.get(i)));

			System.out.println("Step: " + i + "\ndt: " + dt + "\n\td_x:"
					+ dx_disp.get(i) + "\n\td_y: " + dy_disp.get(i));
		}

		// compose total displacement
		double distance = 0;
		if (settings.getBoolean("PathMode", false)) {
			// vector addition, constructing R
			System.out.println("Composing R...\n");
			float rx = 0; // *
			float ry = 0; // *[x, y]*//

			for (int i = 0; i < STEPS - 1; i++) {
				rx += dx_disp.get(i);
				ry += dy_disp.get(i);
				System.out.println("Step: " + i + "\n\tr_x: " + rx
						+ "\n\tr_y: " + ry);
			}

			// Distance formula, constructing D
			// D = sqrt(X^2 + Y^2 )
			return Math.sqrt(rx * rx + ry * ry);
		}

		else {
			// sum up individual distances, constructing D
			for (int i = 0; i < STEPS - 1; i++) {
				// dD = sqrt( dx^2 + dy^2 )
				distance += Math.sqrt(dx_disp.get(i) * dx_disp.get(i)
						+ dy_disp.get(i) * dy_disp.get(i));
			}
			return distance;
		}

	}

	public double Improved_EulersXYZ(ArrayList<Float> x_accel,
			ArrayList<Float> y_accel, ArrayList<Float> z_accel,
			ArrayList<Float> t) {
		if (t == null)
			return -1;
		System.out.println("Entering Improved Eulers Distance method");

		ArrayList<Float> dx_veloc = new ArrayList<Float>();
		ArrayList<Float> dy_veloc = new ArrayList<Float>();
		ArrayList<Float> dz_veloc = new ArrayList<Float>();

		ArrayList<Float> x_veloc = new ArrayList<Float>();
		x_veloc.add(0f);
		ArrayList<Float> y_veloc = new ArrayList<Float>();
		y_veloc.add(0f);
		ArrayList<Float> z_veloc = new ArrayList<Float>();
		z_veloc.add(0f);

		int STEPS = x_accel.size();
		float dt;

		for (int i = 0; i < STEPS - 1; i++) {
			// dx'_i = dx''_(i-1) * dt + (.5) * dt * (dx''(i) - dx''(i-1))
			// dy'_i = dy''_(i-1) * dt + (.5) * dt * (dy''(i) - dy''(i-1))
			// dz'_i = dz''_(i-1) * dt + (.5) * dt * (dz''(i) - dz''(i-1))

			dt = t.get(i + 1) - t.get(i);
			dx_veloc.add(x_accel.get(i) * dt + (.5f) * dt
					* (x_accel.get(i + 1) - x_accel.get(i)));
			dy_veloc.add(y_accel.get(i) * dt + (.5f) * dt
					* (y_accel.get(i + 1) - y_accel.get(i)));
			dz_veloc.add(z_accel.get(i) * dt + (.5f) * dt
					* (y_accel.get(i + 1) - z_accel.get(i)));

			System.out.println("Step: " + i + "\ndt: " + dt + "\n\tv_x: "
					+ dx_veloc.get(i) + "\n\tv_y: " + dy_veloc.get(i)
					+ "\n\tv_z: " + dz_veloc.get(i));
		}

		// Sum up all delta values
		float temp = 0f;
		for (float d : dx_veloc) {
			temp += d;
			x_veloc.add(temp);
		}

		temp = 0;
		for (float d : dy_veloc) {
			temp += d;
			y_veloc.add(temp);
		}

		temp = 0;
		for (float d : dz_veloc) {
			temp += d;
			z_veloc.add(temp);
		}

		ArrayList<Float> dx_disp = new ArrayList<Float>();
		ArrayList<Float> dy_disp = new ArrayList<Float>();
		ArrayList<Float> dz_disp = new ArrayList<Float>();

		ArrayList<Float> x_disp = new ArrayList<Float>();
		x_disp.add(0f);
		ArrayList<Float> y_disp = new ArrayList<Float>();
		y_disp.add(0f);
		ArrayList<Float> z_disp = new ArrayList<Float>();
		z_disp.add(0f);

		// compose displacement
		STEPS = x_veloc.size();
		System.out.println("Composing Displacement from Velocity...\n");
		for (int i = 0; i < STEPS - 1; i++) {
			// dx_i = dx'_(i-1) * dt + (.5) * dt * (dx'(i) - dx'(i-1))
			// dy_i = dy'_(i-1) * dt + (.5) * dt * (dy'(i) - dy'(i-1))
			// dz_i = dz'_(i-1) * dt + (.5) * dt * (dz'(i) - dz'(i-1))

			dt = t.get(i + 1) - t.get(i);
			dx_disp.add(x_veloc.get(i) * dt + (.5f) * dt
					* (x_veloc.get(i + 1) - x_veloc.get(i)));
			dy_disp.add(y_veloc.get(i) * dt + (.5f) * dt
					* (y_veloc.get(i + 1) - y_veloc.get(i)));
			dz_disp.add(z_veloc.get(i) * dt + (.5f) * dt
					* (z_veloc.get(i + 1) - z_veloc.get(i)));

			System.out.println("Step: " + i + "\ndt: " + dt + "\n\td_x: "
					+ dx_disp.get(i) + "\n\td_y: " + dy_disp.get(i)
					+ "\n\td_z: " + dz_disp.get(i));
		}

		// compose total displacement
		double distance = 0;
		if (settings.getBoolean("PathMode", false)) {
			// vector addition, constructing R
			System.out.println("Composing R...\n");
			float rx = 0; // *
			float ry = 0; // *[x, y]*//
			float rz = 0; //

			for (int i = 0; i < STEPS - 1; i++) {
				rx += dx_disp.get(i);
				ry += dy_disp.get(i);
				rz += dz_disp.get(i);
				System.out.println("Step: " + i + "\n\tr_x: " + rx
						+ "\n\tr_y: " + ry + "\n\tr_z: " + rz);
			}

			// Distance formula, constructing D
			// D = sqrt(X^2 + Y^2 + Z^2)
			return Math.sqrt(rx * rx + ry * ry + rz * rz);
		}

		else {
			// sum up individual distances, constructing D
			for (int i = 0; i < STEPS - 1; i++) {
				// dD = sqrt( dx^2 + dy^2 + dz^2 )
				distance += Math.sqrt(dx_disp.get(i) * dx_disp.get(i)
						+ dy_disp.get(i) * dy_disp.get(i) + dz_disp.get(i)
						* dz_disp.get(i));
			}
			return distance;
		}

	}

	public float SimpsonsX(	ArrayList<Float> x_accel,
							ArrayList<Float> t) {

		if (t == null) return -1;

		System.out.println("Entering Simpsons Distance method");

		ArrayList<Float> dx_veloc = new ArrayList<Float>();

		ArrayList<Float> x_veloc = new ArrayList<Float>(); x_veloc.add(0f);

		// compose velocity
		int STEPS = x_accel.size();

		float 	dt,

				k1_x, k2_x, k3_x,

				dv_x;

		System.out.println("Composing Velocity from Acceleration...\n");
		for (int i = 0; i < STEPS - 1; i++) {
			/*
			 * dF = (k1 + 4 * k2 + k3) / 6;
			 * 
			 * k1 = Left_Eulers_Increment(F) = f"(t)*(dt) k2 =
			 * Midpoint_Increment(F) = (f"(t) + f"(t+dt)) / 2 *(dt) k3 =
			 * Right_Eulers_Increment(F+dt) = f"(t+dt) * (dt)
			 */

			dt = t.get(i + 1) - t.get(i);

			// Left_Eulers_Increment
			k1_x = x_accel.get(i) * dt;
			
			// Midpoint_Increment
			k2_x = (x_accel.get(i) + x_accel.get(i + 1)) / 2 * dt;

			// Right_Eulers_Increment
			k3_x = x_accel.get(i + 1) * dt;

			dv_x = (1f / 6f) * (k1_x + 4 * k2_x + k3_x);

			dx_veloc.add(dv_x);

			System.out.println("Step: " + i 
								+ "\ndt: " + dt 
								+ "\n\tv_x: " + dx_veloc.get(i) );
		}
		float temp = 0f;
		for (float d : dx_veloc) {
			temp += d;
			x_veloc.add(temp);
		}

		ArrayList<Float> dx_disp = new ArrayList<Float>();

		System.out.println("Composing Displacement from Velocity...\n");
		STEPS = x_veloc.size();

		float dD_x;
		for (int i = 0; i < STEPS - 1; i++) {

			/*
			 * dF = (k1 + 4 * k2 + k3) / 6;
			 * 
			 * k1 = Left_Eulers_Increment(F) = f"(t)*(dt) k2 =
			 * Midpoint_Increment(F) = (f"(t) + f"(t+dt)) / 2 *(dt) k3 =
			 * Right_Eulers_Increment(F+dt) = f"(t+dt) * (dt)
			 * 
			 */

			dt = t.get(i + 1) - t.get(i);

			// Left_Eulers_Increment
			k1_x = x_veloc.get(i) * dt;

			// Midpoint_Increment
			k2_x = (x_veloc.get(i) + x_veloc.get(i + 1)) / 2 * dt;

			// Right_Eulers_Increment
			k3_x = x_veloc.get(i + 1) * dt;

			dD_x = (1f / 6f) * (k1_x + 4 * k2_x + k3_x);

			dx_disp.add(dD_x);

			System.out.println("Step: " + i + "\ndt: " + dt 
								+ "\n\td_x: " + dx_disp.get(i));
		}

		// compose total displacement
		float distance = 0;
		STEPS = dx_disp.size();
		// constructing R
		System.out.println("Composing R...\n");
		for (int i = 0; i < STEPS; i++) 
		{
			distance += dx_disp.get(i);
		}

		// Distance formula, constructing D
		// D = sqrt(X^2 + Y^2 + Z^2)
		return distance ;

	}

	public float SimpsonsXY(ArrayList<Float> x_accel, ArrayList<Float> y_accel,
			ArrayList<Float> t) {

		if (t == null)
			return -1;

		System.out.println("Entering Simpsons Distance method");

		ArrayList<Float> dx_veloc = new ArrayList<Float>();
		ArrayList<Float> dy_veloc = new ArrayList<Float>();

		ArrayList<Float> x_veloc = new ArrayList<Float>();
		x_veloc.add(0f);
		ArrayList<Float> y_veloc = new ArrayList<Float>();
		y_veloc.add(0f);

		// compose velocity
		int STEPS = x_accel.size();

		float dt, 

		k1_x, k2_x, k3_x, k1_y, k2_y, k3_y,

		dv_x, dv_y;

		System.out.println("Composing Velocity from Acceleration...\n");
		for (int i = 0; i < STEPS - 1; i++) {
			/*
			 * dF = (k1 + 4 * k2 + k3) / 6;
			 * 
			 * k1 = Left_Eulers_Increment(F) = f"(t)*(dt) k2 =
			 * Midpoint_Increment(F) = (f"(t) + f"(t+dt)) / 2 *(dt) k3 =
			 * Right_Eulers_Increment(F+dt) = f"(t+dt) * (dt)
			 */

			dt = t.get(i + 1) - t.get(i);

			// Left_Eulers_Increment
			k1_x = x_accel.get(i) * dt;
			k1_y = y_accel.get(i) * dt;

			// Midpoint_Increment
			k2_x = (x_accel.get(i) + x_accel.get(i + 1)) / 2 * dt;
			k2_y = (y_accel.get(i) + y_accel.get(i + 1)) / 2 * dt;

			// Right_Eulers_Increment
			k3_x = x_accel.get(i + 1) * dt;
			k3_y = y_accel.get(i + 1) * dt;

			dv_x = (1f / 6f) * (k1_x + 4 * k2_x + k3_x);
			dv_y = (1f / 6f) * (k1_y + 4 * k2_y + k3_y);

			dx_veloc.add(dv_x);
			dy_veloc.add(dv_y);

			System.out.println("Step: " + i + "\ndt: " + dt + "\n\tv_x: "
					+ dx_veloc.get(i) + "\n\tv_y: " + dy_veloc.get(i));
		}
		float temp = 0f;
		for (float d : dx_veloc) {
			temp += d;
			x_veloc.add(temp);
		}

		temp = 0;
		for (float d : dy_veloc) {
			temp += d;
			y_veloc.add(temp);
		}

		ArrayList<Float> dx_disp = new ArrayList<Float>();
		ArrayList<Float> dy_disp = new ArrayList<Float>();

		System.out.println("Composing Displacement from Velocity...\n");
		STEPS = x_veloc.size();

		float dD_x, dD_y;
		for (int i = 0; i < STEPS - 1; i++) {

			/*
			 * dF = (k1 + 4 * k2 + k3) / 6;
			 * 
			 * k1 = Left_Eulers_Increment(F) = f"(t)*(dt) k2 =
			 * Midpoint_Increment(F) = (f"(t) + f"(t+dt)) / 2 *(dt) k3 =
			 * Right_Eulers_Increment(F+dt) = f"(t+dt) * (dt)
			 */

			dt = t.get(i + 1) - t.get(i);

			// Left_Eulers_Increment
			k1_x = x_veloc.get(i) * dt;
			k1_y = y_veloc.get(i) * dt;

			// Midpoint_Increment
			k2_x = (x_veloc.get(i) + x_veloc.get(i + 1)) / 2 * dt;
			k2_y = (y_veloc.get(i) + y_veloc.get(i + 1)) / 2 * dt;

			// Right_Eulers_Increment
			k3_x = x_veloc.get(i + 1) * dt;
			k3_y = y_veloc.get(i + 1) * dt;

			dD_x = (1f / 6f) * (k1_x + 4 * k2_x + k3_x);
			dD_y = (1f / 6f) * (k1_y + 4 * k2_y + k3_y);

			dx_disp.add(dD_x);
			dy_disp.add(dD_y);

			System.out.println("Step: " + i + "\ndt: " + dt + "\n\td_x: "
					+ dx_disp.get(i) + "\n\td_y: " + dy_disp.get(i));
		}

		// compose total displacement
		float distance = 0;
		STEPS = dx_disp.size();
		if (settings.getBoolean("PathMode", false)) {
			// vector addition, constructing R
			System.out.println("Composing R...\n");
			float rx = 0;
			float ry = 0;
			for (int i = 0; i < STEPS; i++) {
				rx += dx_disp.get(i);
				ry += dy_disp.get(i);
			}

			// Distance formula, constructing D
			// D = sqrt(X^2 + Y^2 + Z^2)
			return rx * rx + ry * ry;
		}

		else {
			// sum up individual distances, constructing D
			for (int i = 0; i < STEPS; i++) {
				// dD = sqrt( dx^2 + dy^2 )
				distance += Math.sqrt(Math.pow(dx_disp.get(i), 2)
						+ Math.pow(dy_disp.get(i), 2));
			}
			return distance;
		}
	}

	public float SimpsonsXYZ(ArrayList<Float> x_accel,
			ArrayList<Float> y_accel, ArrayList<Float> z_accel,
			ArrayList<Float> t) {

		if (t == null)
			return -1;

		System.out.println("Entering Simpsons Distance method");

		ArrayList<Float> dx_veloc = new ArrayList<Float>();
		ArrayList<Float> dy_veloc = new ArrayList<Float>();
		ArrayList<Float> dz_veloc = new ArrayList<Float>();

		ArrayList<Float> x_veloc = new ArrayList<Float>();
		x_veloc.add(0f);
		ArrayList<Float> y_veloc = new ArrayList<Float>();
		y_veloc.add(0f);
		ArrayList<Float> z_veloc = new ArrayList<Float>();
		z_veloc.add(0f);

		// compose velocity
		int STEPS = x_accel.size();

		float dt,

		k1_x, k2_x, k3_x, k1_y, k2_y, k3_y, k1_z, k2_z, k3_z,

		dv_x, dv_y, dv_z;

		System.out.println("Composing Velocity from Acceleration...\n");
		for (int i = 0; i < STEPS - 1; i++) {
			/*
			 * dF = (k1 + 4 * k2 + k3) / 6;
			 * 
			 * k1 = Left_Eulers_Increment(F) = f"(t)*(dt) k2 =
			 * Midpoint_Increment(F) = (f"(t) + f"(t+dt)) / 2 *(dt) k3 =
			 * Right_Eulers_Increment(F+dt) = f"(t+dt) * (dt)
			 */

			dt = t.get(i + 1) - t.get(i);

			// Left_Eulers_Increment
			k1_x = x_accel.get(i) * dt;
			k1_y = y_accel.get(i) * dt;
			k1_z = z_accel.get(i) * dt;

			// Midpoint_Increment
			k2_x = (x_accel.get(i) + x_accel.get(i + 1)) / 2 * dt;
			k2_y = (y_accel.get(i) + y_accel.get(i + 1)) / 2 * dt;
			k2_z = (z_accel.get(i) + z_accel.get(i + 1)) / 2 * dt;

			// Right_Eulers_Increment
			k3_x = x_accel.get(i + 1) * dt;
			k3_y = y_accel.get(i + 1) * dt;
			k3_z = z_accel.get(i + 1) * dt;

			dv_x = (1f / 6f) * (k1_x + 4 * k2_x + k3_x);
			dv_y = (1f / 6f) * (k1_y + 4 * k2_y + k3_y);
			dv_z = (1f / 6f) * (k1_z + 4 * k2_z + k3_z);

			dx_veloc.add(dv_x);
			dy_veloc.add(dv_y);
			dy_veloc.add(dv_z);

			System.out.println("Step: " + i + "\ndt: " + dt + "\n\tv_x: "
					+ dx_veloc.get(i) + "\n\tv_y: " + dy_veloc.get(i)
					+ "\n\tv_z: " + dz_veloc.get(i));
		}
		float temp = 0f;
		for (float d : dx_veloc) {
			temp += d;
			x_veloc.add(temp);
		}

		temp = 0;
		for (float d : dy_veloc) {
			temp += d;
			y_veloc.add(temp);
		}

		temp = 0;
		for (float d : dy_veloc) {
			temp += d;
			z_veloc.add(temp);
		}

		ArrayList<Float> dx_disp = new ArrayList<Float>();
		ArrayList<Float> dy_disp = new ArrayList<Float>();
		ArrayList<Float> dz_disp = new ArrayList<Float>();

		System.out.println("Composing Displacement from Velocity...\n");
		STEPS = x_veloc.size();

		float dD_x, dD_y, dD_z;
		for (int i = 0; i < STEPS - 1; i++) {

			/*
			 * dF = (k1 + 4 * k2 + k3) / 6;
			 * 
			 * k1 = Left_Eulers_Increment(F) = f"(t)*(dt) k2 =
			 * Midpoint_Increment(F) = (f"(t) + f"(t+dt)) / 2 *(dt) k3 =
			 * Right_Eulers_Increment(F+dt) = f"(t+dt) * (dt)
			 */

			dt = t.get(i + 1) - t.get(i);

			// Left_Eulers_Increment
			k1_x = x_veloc.get(i) * dt;
			k1_y = y_veloc.get(i) * dt;
			k1_z = z_veloc.get(i) * dt;

			// Midpoint_Increment
			k2_x = (x_veloc.get(i) + x_veloc.get(i + 1)) / 2 * dt;
			k2_y = (y_veloc.get(i) + y_veloc.get(i + 1)) / 2 * dt;
			k2_z = (z_veloc.get(i) + z_veloc.get(i + 1)) / 2 * dt;

			// Right_Eulers_Increment
			k3_x = x_veloc.get(i + 1) * dt;
			k3_y = y_veloc.get(i + 1) * dt;
			k3_z = z_veloc.get(i + 1) * dt;

			dD_x = (1f / 6f) * (k1_x + 4 * k2_x + k3_x);
			dD_y = (1f / 6f) * (k1_y + 4 * k2_y + k3_y);
			dD_z = (1f / 6f) * (k1_z + 4 * k2_z + k3_z);

			dx_disp.add(dD_x);
			dy_disp.add(dD_y);
			dy_disp.add(dD_z);

			System.out.println("Step: " + i + "\ndt: " + dt + "\n\td_x: "
					+ dx_disp.get(i) + "\n\td_y: " + dy_disp.get(i)
					+ "\n\td_z: " + dz_disp.get(i));
		}

		// compose total displacement
		float distance = 0;
		STEPS = dx_disp.size();
		if (settings.getBoolean("PathMode", false)) {
			// vector addition, constructing R
			System.out.println("Composing R...\n");
			float rx = 0;
			float ry = 0;
			float rz = 0;
			for (int i = 0; i < STEPS; i++) {
				rx += dx_disp.get(i);
				ry += dy_disp.get(i);
				rz += dz_disp.get(i);
			}

			// Distance formula, constructing D
			// D = sqrt(X^2 + Y^2 + Z^2)
			return rx * rx + ry * ry + rz * rz;
		}

		else {
			// sum up individual distances, constructing D
			for (int i = 0; i < STEPS; i++) {
				// dD = sqrt( dx^2 + dy^2 )
				distance += Math.sqrt(Math.pow(dx_disp.get(i), 2)
						+ Math.pow(dy_disp.get(i), 2));
			}
			return distance;
		}
	}

	public void RemoveGravity(ArrayList<Float>... args) {
		// Very basic. Simply subtracts calibrated gravity from all measurements
		System.out.println("Entering RemoveGravity");

		// args[0] is xData
		// args[1] is yData
		// args[2] is zData

		int count = args[0].size();
		float Gx = settings.getFloat("Gravity_x", 0);
		float Gy = settings.getFloat("Gravity_y", 0);
		float Gz = settings.getFloat("Gravity_z", 0);

		for (int i = 0; i < count; i++) {
			args[0].set(i, args[0].get(i) - Gx);
			if (args.length >= 2)
				args[1].set(i, args[1].get(i) - Gy);
			if (args.length >= 3)
				args[2].set(i, args[2].get(i) - Gz);
		}

	}

	public void removeOutliers(ArrayList<Float> xData, ArrayList<Float> yData) {

		for (int i = 1; i < xData.size(); i++) {
			// TODO: check i-1, i, and i+1, decide if its an outlier, change it
			// if it is.

		}

	}

}
