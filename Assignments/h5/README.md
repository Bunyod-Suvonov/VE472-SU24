## Ex 1.

1. High precision data gives us more accurate information. When the data is not precise enough, especially in big data context, tiny difference from the actual precise data can lead to huge errors in analysis results. So it's important to have precise data to extract accurate information. However, having a higher precision data types such as double instead of float or integer is memory costly. For instance, double uses 8 bytes, whereas int uses 4 bytes to represent values. Twice as much.

2. 

~~~matlab
% Generating 100 random 1000x100 matrices
X = randn(1000, 100, 100);

% Initialize timers
time_svd_X = 0;
time_svd_XT = 0;
time_eig_XXT = 0;
time_eig_XTX = 0;

for i = 1:100
    Xi = X(:,:,i);
    
    tic;
    svd(Xi);
    time_svd_X = time_svd_X + toc;
    
    tic;
    svd(Xi');
    time_svd_XT = time_svd_XT + toc;
    
    tic;
    eig(Xi*Xi');
    time_eig_XXT = time_eig_XXT + toc;
    
    tic;
    eig(Xi'*Xi);
    time_eig_XTX = time_eig_XTX + toc;
end

fprintf('Total time for SVD of X: %f seconds\n', time_svd_X);
fprintf('Total time for SVD of X^T: %f seconds\n', time_svd_XT);
fprintf('Total time for Eigenvalues of XX^T: %f seconds\n', time_eig_XXT);
fprintf('Total time for Eigenvalues of X^TX: %f seconds\n', time_eig_XTX);

Total time for SVD of X: 0.190067 seconds
Total time for SVD of X^T: 0.174278 seconds
Total time for Eigenvalues of XX^T: 4.380432 seconds
Total time for Eigenvalues of X^TX: 0.072026 seconds
~~~

Wow, XX^T does take longer but didn't know there would be this much of a difference in their runtime. This is due to the resultant matrix sizes of multiplication of X and X^T in different orders. XX^T leads to 1000X1000 matrix whereas X^TX leads to 100X100 matrix

3.
~~~matlab
% Given matrix X
X = [-9 11 -21 63 -252;
     70 -69 141 -421 1684;
     -575 575 -1149 3451 -13801;
     3891 -3891 7782 -23345 93365;
     1024 -1024 2048 -6144 24572];

% Number of tests to perform
num_tests = 1000;

% Initialize matrices to store eigenvalues and singular values
eigenvalues = zeros(num_tests, size(X, 1));
singularvalues = zeros(num_tests, min(size(X)));

for i = 1:num_tests
    % Generate a small random perturbation
    deltaX = eps * randn(size(X));
    
    % Add the perturbation to X
    perturbedX = X + deltaX;
    
    % Calculate the eigenvalues of the perturbed matrix
    eigenvalues(i, :) = eig(perturbedX);
    
    % Calculate the singular values of the perturbed matrix
    singularvalues(i, :) = svd(perturbedX);
end

% Calculate the standard deviation of the eigenvalues and singular values
eigenvalue_variation = std(eigenvalues);
singularvalue_variation = std(singularvalues);

% Display the results without scientific notation
fprintf('Eigenvalue variation: \n');
for i = 1:length(eigenvalue_variation)
    fprintf('%.20f\n', eigenvalue_variation(i));
end

fprintf('Singular value variation: \n');
for i = 1:length(singularvalue_variation)
    fprintf('%.20f\n', singularvalue_variation(i));
end


Eigenvalue variation: 
0.00000000000000008396
0.00000000000000008396
0.00000000000000006055
0.00000000000000006055
0.00000000000000017356
Singular value variation: 
0.00000000056780866925
0.00000000000000177725
0.00000000000000510958
0.00000000000000288802
0.00000000000000000000
~~~
4.
2) Wow, XX^T does take longer but didn't know there would be this much of a difference in their runtime. This is due to the resultant matrix sizes of multiplication of X and X^T in different orders. XX^T leads to 1000X1000 matrix whereas X^TX leads to 100X100 matrix.
3) The variations of both Eigenvalues and singular values are very less according to the results. Singular values' variations are in 10^-10s and Eigenvalues' variations are in 10^-16, which means both of them are highly stable despite the small perturbation.

## Ex 2.

Please see ex2.pdf

## Ex 3.

Please see ex4.ipynb