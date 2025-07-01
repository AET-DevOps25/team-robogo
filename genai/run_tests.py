#!/usr/bin/env python3
"""
Test runner script for the genai module.
"""
import subprocess
import sys
import os

def run_tests():
    """Run all tests using pytest"""
    # Change to the genai directory
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    
    # Run pytest
    result = subprocess.run([
        sys.executable, "-m", "pytest", 
        "tests/", 
        "-v", 
        "--tb=short"
    ])
    
    return result.returncode

if __name__ == "__main__":
    exit_code = run_tests()
    sys.exit(exit_code) 