#!/usr/bin/python
# -*- coding: utf-8 -*-

import sys
import os.path
import tempfile
import unittest

sys.path.insert(1, os.path.join(sys.path[0], '../src'))

from Movement import Movement

class TestMovement(unittest.TestCase):

    def setUp( self ):
        pass

    def test_Movement(self):
        direction = Movement.DOWN
        self.assertEqual( Movement.DOWN, direction )

if __name__ == '__main__':
    unittest.main()
