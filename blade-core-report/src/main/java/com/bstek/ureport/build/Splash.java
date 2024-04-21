/*******************************************************************************
 * Copyright 2017 Bstek
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.bstek.ureport.build;

/**
 * @author Jacky.gao
 * @since 2017年6月19日
 */
public class Splash {
	public void doPrint() {
		String sb = "\n" +
			" ___  ___  ________  _______   ________  ________  ________  _________        ________     \n" +
			"|\\  \\|\\  \\|\\   __  \\|\\  ___ \\ |\\   __  \\|\\   __  \\|\\   __  \\|\\___   ___\\     |\\_____  \\    \n" +
			"\\ \\  \\\\\\  \\ \\  \\|\\  \\ \\   __/|\\ \\  \\|\\  \\ \\  \\|\\  \\ \\  \\|\\  \\|___ \\  \\_|     \\|____|\\ /_   \n" +
			" \\ \\  \\\\\\  \\ \\   _  _\\ \\  \\_|/_\\ \\   ____\\ \\  \\\\\\  \\ \\   _  _\\   \\ \\  \\            \\|\\  \\  \n" +
			"  \\ \\  \\\\\\  \\ \\  \\\\  \\\\ \\  \\_|\\ \\ \\  \\___|\\ \\  \\\\\\  \\ \\  \\\\  \\|   \\ \\  \\          __\\_\\  \\ \n" +
			"   \\ \\_______\\ \\__\\\\ _\\\\ \\_______\\ \\__\\    \\ \\_______\\ \\__\\\\ _\\    \\ \\__\\        |\\_______\\\n" +
			"    \\|_______|\\|__|\\|__|\\|_______|\\|__|     \\|_______|\\|__|\\|__|    \\|__|        \\|_______|\n" +
			"........................................................................................................" +
			"\n" +
			".  uReport, is a Chinese style report engine" +
			" licensed under the Apache License 2.0,                    ." +
			"\n" +
			".  which is opensource, easy to use,high-performance, with browser-based-designer,                     ." +
			"\n" +
			".  it has now been upgraded by BladeX to support jdk 17 and spring boot 3.                             ." +
			"\n" +
			"........................................................................................................" +
			"\n";
		System.out.println(sb);
	}


}
