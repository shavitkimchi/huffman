# Project 6: Huffman Coding/Compression, Fall 2024

See the [details document](docs/details.md) for information on using Git, starting the project, and more details about the project including information about the classes and concepts that are outlined briefly below. You'll absolutely need to read the information in the details document to understand how the classes in this project work independently and together. The details document also contains project-specific details. This current document provides a high-level overview of the assignment.

You are STRONGLY encouraged to work with a partner on 6! (as you were on P5 and will be on P7). See the [details document](docs/details.md) for information on using Git with a partner and how the workflow can proceed. If you'd like to be paired (somewhat randomly, but you can write about yourself or a partner) then fill out [this form](https://forms.office.com/r/GTJWbG8Eyn) to request a pairing.


## Outline 

- [Project Introduction](#project-introduction)
- [Part 0: Understanding and Running Starter Code](#part-0-understanding-and-running-starter-code)
- [Part 1: Implementing `HuffProcessor.decompress`](#part-1-implementing-huffprocessordecompress)
- [Part 2: Implementing `HuffProcessor.compress`](#part-2-implementing-huffprocessorcompress)
- [Analysis](#analysis)
- [Submitting and Grading](#submitting-and-grading)

## Project Introduction

There are many techniques used to compress digital data (that is, to represent it using less memory). This assignment covers Huffman Coding, which is used everywhere from zipping a folder to jpeg and mp3 encodings. See the [details document](docs/details.md) for background on how the compression algorith was developed. **You should definitely use that document for background before programming.**

When you've read the description of the algorithm and data structures used you'll be ready to implement both decompression (a.k.a. uncompressing) and compression  using Huffman Coding. You'll be using input and output or I/O classes that read and write 1 to many bits at a time, i.e., a single zero or one to several zeros and ones. This will make debugging your program a challenge.

### Post-reading/pre-coding self-assessment questions

1. Why are two passes over the input file to be compressed required when creating a compressed version of the input file?
2. What aspects of creating the Huffman tree from counts account for that process being a greedy algorithm?
3. At a high-level, how is the tree used to create 8-bit char/chunk encodings?
4. What is written first, after the magic number, in the compressed file?
5. Why are the bits written at the end of the compressed file representing PSEUDO_EOF required?
6. After reading the magic number and tree, how are the bits representing compressed data read when decompressing, e.g., how many bits are read each time the compressed data is accessed?


## Part 0: Understanding and Running Starter Code

Once you understand the Huffman coding algorithm, you should review this section to understand the organization of the starter code. For details on the `BitInputStream` and `BitOutputStream` classes, see [the details document](docs/details.md).


### Running Starter Code (with incomplete `HuffProcessor.decompress`)

**Run `HuffMainDecompress`**. This prompts for a file to decompress, then calls `HuffProcessor.decompress`. You're given a stub version of that method; it initially ***simply copies the first file to another file***, it doesn’t actually decompress it. To make sure you know how to use this program, we recommend you run the program as you fork/cloned it and as described below.

Choose `mystery.tif.hf` from the data folder to decompress (the `.hf` suffix indicates this has been compressed by a working `HuffProcessor.compress`). When prompted with a name for the file to save, use a `UHF prefix`, i.e., save with the name `UFHmystery.tif.uhf (that suffix is the default).  

Then run `diff` on the command line/terminal (see [the details doc](docs/details.md) for information on using `diff`). Use diff to compare two files: the original, `mystery.tif.hf` and the uncompressed version: `UHFmystery.tif.uhf`. The `diff` program should _indicate_ these files are the same. This is because the code you first get from git simply copies the first file to another file, it doesn't actually decompress it. **You will use `diff` to check whether your implementation is working correctly locally, there are no JUnit tests for this project.**

The main takeaways here in running before implementing `HuffProcess.decompress` are to 
- Understand what to run when decompressing.
- Understand how to use `diff` on the command line to compare files. 



## Part 1: Implementing `HuffProcessor.decompress`

You should begin programming by implementing `decompress` first before moving on to `compress`. You'll remove the code you're given intially in `HuffProcessor.decompress` and implement code to actually **decompress** as described in this section. You **must remember to close the output file** before `decompress` returns. The call `out.close()` is in the code you're given, be sure it's in the code you write as well.

There are four conceptual steps in decompressing a file that has been compressed using Huffman coding, you can see these
in the [details document](docs/details.md).  

- Read the magic number
- Read the tree used for compression and decompression
- Read the compressed bits one-at-a-time using the tree to find characters
- Close the input file


## Part 2: Implementing `HuffProcessor.compress`

There are five conceptual steps to compress a file using Huffman coding. You do not need to use helper methods for these steps, but for some steps helper methods are **extremely useful and will facilitate debugging**. These steps are outlined in the [details document](docs/details.md) in much more detail.

- Count how many times every chunk/character occurs (read input file)
- Create tree from these counts using a greedy algorithm
- Create encodings (a map) for every chunk/character using the tree
- Write header information to the compressed file 
- Read the input file again, writing encodings, including `PSEUDO_EOF`


**See the [appendix section in the details.md](docs/details.md) file for important information on understanding this compression algorithm.

## Analysis

You'll submit the analysis as a PDF separate from the code in Gradescope. If you are working with a partner, you and your partner should submit a single analysis document.

For the analysis questions, we will let $`N`$ be the number of total characters in a file to encode, and let $`M`$ be the total number of *unique* characters in the file. Note that both refer to the *non-compressed file*. Note that $`M \leq N`$. Define the *compression ratio* of a file to be the number of bits in the original file divided by the number of bits in the compressed file.

Note that running the `HuffMainCompress` and `HuffMainDecompress` programs will print information to the terminal about the number of bits and the runtime of the compress and decompress algorithms.

**Question 1.** Suppose you want to compress two different files: `fileA` and `fileB`. Both have $`N`$ total characters and $`M`$ unique characters. The characters in `fileA` follow a uniform distribution, meaning each of the unique characters appears $`N/M`$ times. In `fileB`, the $`i`$'th unique character appears $`2^i`$ times (and the numbers add up to $`N`$), so some characters are much more common than others. Which file should achieve a higher compression ratio? Explain your answer. **The compression ratio is the size of the original file divided by the size of the compressed file.**

**Question 2.** Typically the number of total characters  $`N`$ is much greater than the number of unique characters, $`M`$ which is at most 256 when reading 8-bit chunks as with Huffman coding. Files are read twice when compressing: once to count/determine encodings, and once to actually write the encodings. Run your compression code on two images from the `data` folder: `oak-ridge.jpg` and `mtblanc.jpg`. In your analysis, include the time it takes to compress each of these files, what the compression ratio is: size of original/size of compressed. Do the same for the text file `hawthorne.txt` Can you make any conclusions about compression of images compared to text files? About the time to run compression on files? Discuss and justify your answers based on empirical/runtime data. 

**Question 3.** When running `decompress`, each character that is decompressed requires traversing nodes in the Huffman coding tree, and there are $`N`$ such characters. Run your `decompress` code on the images you compressed in the previous question. Make some conclusions about the runtimes of compressed images compared to compressed text files when decompressing? Justify your answers by runtimes as you can.

## Submitting and Grading

Push your code to Git. Do this often. To submit:

1. Submit your code on gradescope to the autograder. If you are working with a partner, refer to [this document](https://docs.google.com/document/d/e/2PACX-1vREK5ajnfEAk3FKjkoKR1wFtVAAEN3hGYwNipZbcbBCnWodkY2UI1lp856fz0ZFbxQ3yLPkotZ0U1U1/pub) for submitting to Gradescope with a partner. 
2. Submit a PDF to Gradescope in the separate Analysis assignment. Be sure to mark pages for the questions as explained in the [gradescope documentation here](https://help.gradescope.com/article/ccbpppziu9-student-submit-work#submitting_a_pdf). If you are working with a partner, you should submit a single document and [add your partner to your group on gradescope](https://help.gradescope.com/article/m5qz2xsnjy-student-add-group-members).

Points are awarded equally for compression and decompression. You'll get points for decompressing and compressing text and image files. These are 10 points each, for a total of 40 points possible on the code. The analysis is scored separately by TAs for a total of 12 points (4 per question).

