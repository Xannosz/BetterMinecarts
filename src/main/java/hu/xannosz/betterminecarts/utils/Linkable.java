/*############################################################
This file copied from Cammies-Minecart-Tweaks (https://github.com/CammiePone/Cammies-Minecart-Tweaks)

Copyright (C) 2022 Cammie

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to use, copy, modify, and/or merge copies of the
Software, and to permit persons to whom the Software is furnished to do so, subject to the following
restrictions:

 1) The above copyright notice and this permission notice shall be included in all copies or substantial
    portions of the Software.
 2) You include attribution to the copyright holder(s) in public display of any project that uses any
    portion of the Software.
 3) You may not publish or distribute substantial portions of the Software in its compiled or uncompiled
    forms without prior permission from the copyright holder.
 4) The Software does not make up a substantial portion of your own projects.

If more than 2 years have passed without any source code change on the origin repository for the Software
(https://github.com/CammiePone/Cammies-Minecart-Tweaks), then all but the first 2 restrictions are void, and
may be ignored when using, copying, modifying, and/or merging copies of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
USE OR OTHER DEALINGS IN THE SOFTWARE.

Modified by Xannosz 2022-2023
############################################################*/
package hu.xannosz.betterminecarts.utils;

import net.minecraft.world.entity.vehicle.AbstractMinecart;

public interface Linkable {
	AbstractMinecart getLinkedParent();

	void setLinkedParent(AbstractMinecart parent);

	AbstractMinecart getLinkedChild();

	void setLinkedChild(AbstractMinecart child);

	void updateChains();

	AbstractMinecart getLinkedParentForRender();
}
