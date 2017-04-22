#!/usr/bin/python
# -*- coding: utf-8 -*-

#from mymodule import rm

import os.path
import tempfile
import unittest

class TestExample(unittest.TestCase):

    def setUp( self ):
        pass

    def test_strings(self):
        my_string = 'Alfred'
        self.assertEqual( 'Alfred', my_string )

if __name__ == '__main__':
    unittest.main()
