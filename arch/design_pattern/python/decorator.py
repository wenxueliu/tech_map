#!/usr/bin/env python
# encoding: utf-8

import functools
def wrapped_partial(func, *args, **kwargs):
  partial_func = functools.partial(func, *args, **kwargs)
    functools.update_wrapper(partial_func, func)
      return partial_func
