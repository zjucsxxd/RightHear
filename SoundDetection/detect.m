
addpath('ParticleFilter/matlab_code')

y = wavread('Jesse_normal.wav');
z = wavread('Janessa.wav');
y = y';
z = z';
y = [y(1, :); z(1, 1:77820)];
y(1, :) = y(1, :) / max(y(1, :));
y(2, :) = y(2, :) / max(y(2, :));
num_particles = 10;

pf = ParticleFilter(size(y, 1), size(y, 2), num_particles);

step_size = int32(size(y, 2)/100);

sigma = 0.01;
for i = 1:100
    frame = y(1, ((i - 1) * step_size + 1):(step_size * i));
    
    diff = calc_diff(y, frame, pf.candidates);
    
    Pz_x  = exp(-diff/(2 * sigma));
    Pz_x  = Pz_x / sum(Pz_x);
    pf.observe(Pz_x);

    pf.elapseTime(@(pos) dynamics_model(pos, step_size, 5));
    
    uint32(pf.center)
    i * step_size
end