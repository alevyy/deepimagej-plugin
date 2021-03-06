/*
 * DeepImageJ
 * 
 * https://deepimagej.github.io/deepimagej/
 *
 * Conditions of use: You are free to use this software for research or educational purposes. 
 * In addition, we strongly encourage you to include adequate citations and acknowledgments 
 * whenever you present or publish results that are based on it.
 * 
 * Reference: DeepImageJ: A user-friendly plugin to run deep learning models in ImageJ
 * E. Gomez-de-Mariscal, C. Garcia-Lopez-de-Haro, L. Donati, M. Unser, A. Munoz-Barrutia, D. Sage. 
 * Submitted 2019.
 *
 * Bioengineering and Aerospace Engineering Department, Universidad Carlos III de Madrid, Spain
 * Biomedical Imaging Group, Ecole polytechnique federale de Lausanne (EPFL), Switzerland
 *
 * Corresponding authors: mamunozb@ing.uc3m.es, daniel.sage@epfl.ch
 *
 */

/*
 * Copyright 2019. Universidad Carlos III, Madrid, Spain and EPFL, Lausanne, Switzerland.
 * 
 * This file is part of DeepImageJ.
 * 
 * DeepImageJ is an open source software (OSS): you can redistribute it and/or modify it under 
 * the terms of the BSD 2-Clause License.
 * 
 * DeepImageJ is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * 
 * You should have received a copy of the BSD 2-Clause License along with DeepImageJ. 
 * If not, see <https://opensource.org/licenses/bsd-license.php>.
 */

package deepimagej.tools;

import ij.IJ;
import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class CompactMirroring {
	
	public static ImagePlus mirrorXY(ImagePlus imp, 
							   int paddingXLeft, int paddingXRight,
							   int paddingYTop, int paddingYBottom) {
		int nx = imp.getWidth();
		int ny = imp.getHeight();
		int nc = imp.getNChannels();
		int nz = imp.getNSlices();
		int nt = imp.getNFrames();
		ImagePlus out = IJ.createImage("Mirror", "32-bits", nx + paddingXLeft + paddingXRight,
										ny + paddingYTop + paddingYBottom, nc, nz, nt);
		for(int c=1; c<=nc; c++)
			for(int z=1; z<=nz; z++)
				for(int t=1; t<=nt; t++) {
					imp.setPositionWithoutUpdate(c, z, t);
					out.setPositionWithoutUpdate(c, z, t);
					ImageProcessor ip = imp.getProcessor();
					ImageProcessor op = mirrorXY(ip, paddingXLeft, paddingXRight,
							   					 paddingYTop, paddingYBottom);
					out.setProcessor(op);
				}
		return out;
	}
	
	private static ImageProcessor mirrorXY(ImageProcessor ip,
								   int paddingXLeft, int paddingXRight,
								   int paddingYTop, int paddingYBottom) {
		int nx = ip.getWidth();
		int ny = ip.getHeight();
		FloatProcessor fp = new FloatProcessor(nx + paddingXLeft + paddingXRight,
												ny + paddingYTop + paddingYBottom);
		int periodX = 2*nx - 2;
		int periodY = 2*ny - 2;
		int xm, ym;
		for(int x = -paddingXLeft; x<nx+paddingXRight; x++) {
			xm = mirror(x, nx, periodX);
			for(int y = -paddingYTop; y<ny+paddingYBottom; y++) {
				ym = mirror(y, ny, periodY);
				double v = ip.getPixelValue(xm, ym);
				fp.putPixelValue(x+paddingXLeft, y+paddingYTop, v);
			}
		}
		return fp;
	}
	
	private static int mirror(int a, int n, int period) {
		while (a < 0)
			a += period;
		while (a >= n) {
			a = period - a;
			a = (a < 0 ? -a : a);
		}
		return a;
	}
}
