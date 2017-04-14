/*
 * Copyright (C) 2017 glbrimhall.com
 *
 * This file is part of an Elevator Simulator program written in java.
 *
 * Elevator Simulator program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * The Movement enum represents an UP or DOWN action with an elevator.
 */
var Movement = Object.freeze( {
   /**
     * Movement enum provides high level control info on state of the elevator
     * UP - elevator is moving up
     * DOWN - elevator is moving down
     * ATFLOOR - elevator is stopped waiting at a floor
     * MAINTENENCE - elevator is offline
     */
    UP   : 1,
    DOWN : 2
} );
